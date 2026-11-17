# Pars AI – AI-Powered Personal Assistant

Pars AI is an AI-powered personal assistant developed using Java, Spring Boot, and Spring AI. The project explores modern AI application development by integrating Large Language Models (LLMs), Retrieval-Augmented Generation (RAG), memory management, and tool calling.

The application runs locally using Ollama and a custom Qwen model.

## Technologies

- Java 21
- Spring Boot
- Spring AI
- Ollama
- Qwen (LLM)
- Nomic Embed Text (Embedding Model)
- Gradle
- H2 Database
- REST API
- HTML, CSS, JavaScript

## Features

- **AI Chat:** Conversational interaction with a locally hosted LLM.
- **Conversation Memory:** Maintains conversation context.
- **Persistent Memory:** Stores relevant information in an H2 database.
- **RAG (Retrieval-Augmented Generation):** Retrieves relevant information from stored documents to provide context-aware answers.
- **Hybrid Search:** Combines keyword-based and vector-based search.
- **Reranking:** Reorders retrieved results based on relevance. Currently disabled by default due to local performance limitations.
- **Tool Calling:** Allows the AI model to interact with application tools, including database operations.
- **Basic Agent Workflow:** Explores AI-driven task execution and tool usage.

## Architecture

The application follows a layered Spring Boot architecture:

- Controller layer – Handles REST API requests.
- Service layer – Manages AI interactions and business logic.
- Configuration layer – Configures Spring AI and Ollama integration.
- Persistence layer – Stores memory and application data using H2.

## Getting Started

### Prerequisites

- Java 21
- Ollama
- Gradle

### Configuration

1. Install and run Ollama.
2. Download the required models:
   - `pars-qwen`
   - `nomic-embed-text`
3. Configure the Ollama connection and database settings in `application.properties`.
4. Use `application-example.properties` as a configuration reference.

### Run the Application

On Windows:

```bash
gradlew.bat bootRun
```

The application will start on the configured Spring Boot port (default: 8080).

## Purpose

This project was developed as a practical learning journey from traditional Java backend development toward AI application development.

It demonstrates hands-on experience with Spring AI, local LLM integration, RAG pipelines, vector search, memory management, and AI tool calling.
