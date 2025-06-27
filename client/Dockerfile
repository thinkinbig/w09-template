# Use a Node.js Alpine image for the builder stage
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build \
 && npm prune --production

# Use a distroless image for the final stage
FROM gcr.io/distroless/nodejs22-debian12:debug-nonroot
WORKDIR /app
COPY --from=builder --chown=1005 /app/build build/
COPY --from=builder --chown=1005 /app/node_modules node_modules/
COPY package.json .

EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=10s --retries=5 CMD wget --spider --quiet http://localhost:3000

ENV NODE_ENV=production
USER 1005
CMD ["build/index.js"]