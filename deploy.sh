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
    
    # Wait for services to be ready with improved retry logic
    echo "⏳ Waiting for MySQL to be ready..."
    local mysql_ready=false
    for i in {1..30}; do
        if docker-compose exec -T mysql mysqladmin ping -h"localhost" --silent 2>/dev/null; then
            mysql_ready=true
            break
        fi
        echo "   Attempt $i/30: MySQL not ready yet, waiting 5 seconds..."
        sleep 5
    done
    
    if [ "$mysql_ready" = false ]; then
        echo "❌ MySQL failed to start after 2.5 minutes"
        docker-compose logs mysql
        exit 1
    fi
    echo "✅ MySQL is ready"
    
    echo "⏳ Waiting for Redis to be ready..."
    local redis_ready=false
    for i in {1..12}; do
        if docker-compose exec -T redis redis-cli ping 2>/dev/null | grep -q PONG; then
            redis_ready=true
            break
        fi
        echo "   Attempt $i/12: Redis not ready yet, waiting 5 seconds..."
        sleep 5
    done
    
    if [ "$redis_ready" = false ]; then
        echo "❌ Redis failed to start after 1 minute"
        docker-compose logs redis
        exit 1
    fi
    echo "✅ Redis is ready"
    
    echo "⏳ Waiting for Spring Boot app to be ready..."
    local app_ready=false
    for i in {1..24}; do
        if curl -f http://localhost:8000/actuator/health >/dev/null 2>&1; then
            app_ready=true
            break
        fi
        echo "   Attempt $i/24: Spring Boot app not ready yet, waiting 10 seconds..."
        sleep 10
    done
    
    if [ "$app_ready" = false ]; then
        echo "❌ Spring Boot app failed to start after 4 minutes"
        echo "📋 App logs:"
        docker-compose logs app
        exit 1
    fi
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