import os
import json
import requests
from typing import Dict, Any, List, Optional
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from langchain.llms.base import LLM
from langchain_core.prompts import PromptTemplate
from langchain.callbacks.manager import CallbackManagerForLLMRun

# Environment configuration
CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = "https://gpu.aet.cit.tum.de/api/chat/completions"

# Create FastAPI application instance
app = FastAPI(
    title="LLM Recommendation Service",
    description="Service that generates personalized food recommendations using an LLM",
    version="1.0.0"
)


class RecommendRequest(BaseModel):
    """
    Request schema for recommendation endpoint.

    Attributes:
        favorite_menu (List[str]): User's favorite meal names
        todays_menu (List[str]): Today's available meal names
    """
    favorite_menu: List[str] = Field(..., description="User's favorite meal names")
    todays_menu: List[str] = Field(..., description="Today's available meal names")


class RecommendResponse(BaseModel):
    """
    Response schema for recommendation endpoint.

    Attributes:
        recommendation (str): The personalized recommendation string.
    """
    recommendation: str = Field(..., description="Personalized food recommendation")


class OpenWebUILLM(LLM):
    """
    Custom LangChain LLM wrapper for Open WebUI API.
    
    This class integrates the Open WebUI API with LangChain's LLM interface,
    allowing us to use the API in LangChain chains and pipelines.
    """
    
    api_url: str = API_URL
    api_key: str = CHAIR_API_KEY
    model_name: str = "llama3:latest"
    
    @property
    def _llm_type(self) -> str:
        return "open_webui"
    
    def _call(
        self,
        prompt: str,
        stop: Optional[List[str]] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        """
        Call the Open WebUI API to generate a response.
        
        Args:
            prompt: The input prompt to send to the model
            stop: Optional list of stop sequences
            run_manager: Optional callback manager for LangChain
            **kwargs: Additional keyword arguments
            
        Returns:
            The generated response text
            
        Raises:
            Exception: If API call fails
        """
        if not self.api_key:
            raise ValueError("CHAIR_API_KEY environment variable is required")
        
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }
        
        # Build messages for chat completion
        messages = [
            {"role": "user", "content": prompt}
        ]
        
        payload = {
            "model": self.model_name,
            "messages": messages,
        }
        
        try:
            response = requests.post(
                self.api_url,
                headers=headers,
                json=payload,
                timeout=30
            )
            response.raise_for_status()
            
            result = response.json()
            
            # Extract the response content
            if "choices" in result and len(result["choices"]) > 0:
                content = result["choices"][0]["message"]["content"]
                return content.strip()
            else:
                raise ValueError("Unexpected response format from API")
                
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")


# Initialize the LLM
llm = OpenWebUILLM()

# Create the prompt template
recommendation_prompt = PromptTemplate(
    input_variables=["favorite_menu", "todays_menu"],
    template="""You are a helpful food recommendation assistant. Your task is to suggest exactly one dish from today's menu based on the user's preferences.

User's favorite meals: {favorite_menu}

Today's available meals: {todays_menu}

Based on the user's favorite meals, please recommend exactly ONE meal from today's available options. 
Consider:
- Similarity to the user's favorite meals
- Flavor profiles that match their preferences
- Availability in today's menu

IMPORTANT: You must respond with ONLY the exact name of one dish from today's menu. Do not include any explanations, additional text, punctuation, or formatting. Just return the dish name exactly as it appears in today's menu.

Example format: Spaghetti Carbonara

Recommendation:"""
)

# Create the chain using the new RunnableSequence pattern
recommendation_chain = recommendation_prompt | llm

@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "LLM Recommendation Service"}


@app.post(
    "/recommend",
    response_model=RecommendResponse,
    summary="Generate personalized food recommendation",
    description="Accepts user's favorite meals and today's menu, returns a personalized meal recommendation via Ollama."
)
async def recommend(req: RecommendRequest) -> RecommendResponse:
    """
    Generate a personalized food recommendation using LangChain and Ollama.
    
    Args:
        req: Request containing user's favorite meals and today's menu
        
    Returns:
        RecommendResponse containing the recommendation
        
    Raises:
        HTTPException: If the API call fails or other errors occur
    """
    try:
        # Validate input
        if not req.favorite_menu:
            raise HTTPException(
                status_code=400, 
                detail="favorite_menu cannot be empty"
            )
        
        if not req.todays_menu:
            raise HTTPException(
                status_code=400, 
                detail="todays_menu cannot be empty"
            )
        
        # Format arrays as comma-separated strings for better processing
        favorite_meals_str = ", ".join(req.favorite_menu)
        todays_meals_str = ", ".join(req.todays_menu)
        
        # TODO Use the chain to generate recommendation
        recommendation = recommendation_chain.invoke({
            "favorite_menu": favorite_meals_str,
            "todays_menu": todays_meals_str
        })
        
        return RecommendResponse(recommendation=recommendation)
        
    except HTTPException:
        # Re-raise HTTP exceptions as-is
        raise
    except Exception as e:
        # Log the error (in production, use proper logging)
        print(f"Error generating recommendation: {str(e)}")
        raise HTTPException(
            status_code=500, 
            detail=f"Failed to generate recommendation: {str(e)}"
        )


@app.get("/")
async def root():
    """Root endpoint with service information."""
    return {
        "service": "LLM Recommendation Service",
        "version": "1.0.0",
        "description": "Generates personalized food recommendations using LangChain and Open WebUI",
        "endpoints": {
            "health": "/health",
            "recommend": "/recommend",
            "docs": "/docs"
        }
    }

# Entry point for direct execution
if __name__ == "__main__":
    """
    Entry point for `python main.py` invocation.
    Starts Uvicorn server serving this FastAPI app.

    Honors PORT environment variable (default: 5000).
    Reload=True enables live-reload during development.
    """
    import uvicorn

    port = int(os.getenv("PORT", 5000))
    
    print(f"Starting LLM Recommendation Service on port {port}")
    print(f"API Documentation available at: http://localhost:{port}/docs")
    
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=port,
        reload=True
    )
