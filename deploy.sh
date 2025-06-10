#!/bin/bash

# STEM Application Docker Deployment Script

set -e

echo "🚀 Starting STEM Application Deployment..."

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        echo "❌ Docker is not running. Please start Docker and try again."
        exit 1
    fi
    echo "✅ Docker is running"
}

# Function to clean up old containers and images
cleanup() {
    echo "🧹 Cleaning up old containers and images..."
    docker-compose down -v --remove-orphans 2>/dev/null || true
    docker system prune -f --volumes
    echo "✅ Cleanup completed"
}

# Function to build and start services
deploy() {
    echo "🔨 Building and starting services..."
    docker-compose up --build -d
    echo "✅ Services started"
}

# Function to show logs
show_logs() {
    echo "📋 Showing logs..."
    docker-compose logs -f
}

# Function to check service health
check_health() {
    echo "🏥 Checking service health..."
    
    # Wait for services to be ready
    echo "⏳ Waiting for MySQL to be ready..."
    until docker-compose exec mysql mysqladmin ping -h"localhost" --silent; do
        sleep 2
    done
    echo "✅ MySQL is ready"
    
    echo "⏳ Waiting for Redis to be ready..."
    until docker-compose exec redis redis-cli ping | grep -q PONG; do
        sleep 2
    done
    echo "✅ Redis is ready"
    
    echo "⏳ Waiting for Spring Boot app to be ready..."
    until curl -f http://localhost:8000/actuator/health >/dev/null 2>&1; do
        sleep 5
    done
    echo "✅ Spring Boot app is ready"
    
    echo "🎉 All services are healthy!"
    echo "🌐 Application available at: http://localhost"
    echo "🔧 Direct app access: http://localhost:8000"
    echo "💾 MySQL available at: localhost:3306"
    echo "🚀 Redis available at: localhost:6379"
}

# Main script logic
case "${1:-deploy}" in
    "deploy")
        check_docker
        cleanup
        deploy
        check_health
        ;;
    "logs")
        show_logs
        ;;
    "stop")
        echo "🛑 Stopping services..."
        docker-compose down
        echo "✅ Services stopped"
        ;;
    "restart")
        echo "🔄 Restarting services..."
        docker-compose restart
        check_health
        ;;
    "clean")
        check_docker
        cleanup
        ;;
    "status")
        docker-compose ps
        ;;
    *)
        echo "Usage: $0 {deploy|logs|stop|restart|clean|status}"
        echo "  deploy  - Build and start all services (default)"
        echo "  logs    - Show logs for all services"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  clean   - Clean up containers and images"
        echo "  status  - Show service status"
        exit 1
        ;;
esac