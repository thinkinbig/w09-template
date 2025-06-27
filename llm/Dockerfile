# Build a virtualenv using the appropriate Debian release
# * Install python3-venv for the built-in Python3 venv module (not installed by default)
# * Install gcc libpython3-dev to compile C Python modules
# * In the virtualenv: Update pip setuputils and wheel to support building new packages
FROM debian:12-slim AS build
RUN apt-get update && \
    apt-get install --no-install-suggests --no-install-recommends --yes python3-venv=3.11.2-1+b1 gcc=4:12.2.0-3 libpython3-dev=3.11.2-1+b1 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    python3 -m venv /venv && \
    /venv/bin/pip install --upgrade pip setuptools wheel

# Build the virtualenv as a separate step: Only re-execute this step when requirements.txt changes
FROM build AS build-venv
COPY requirements.txt /requirements.txt
RUN /venv/bin/pip install --disable-pip-version-check -r /requirements.txt

# Copy the virtualenv into a distroless image
FROM gcr.io/distroless/python3-debian12:debug-nonroot
COPY --from=build-venv --chown=8347 /venv /venv
COPY . /app
WORKDIR /app
USER 8347

EXPOSE 5000
HEALTHCHECK --interval=30s --timeout=10s --retries=5 CMD wget --spider --quiet http://localhost:5000/health
USER nonroot
ENTRYPOINT ["/venv/bin/python3", "main.py"]