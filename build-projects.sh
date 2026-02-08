#!/bin/bash

# Скрипт для скачивания и сборки проектов Food Delivery
# Сохраните как build-projects.sh

set -e  # Прекратить выполнение при ошибке

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Начинаем сборку проектов Food Delivery ===${NC}"

# Определяем корневую директорию (родительскую для всех проектов)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PARENT_DIR="$(dirname "$SCRIPT_DIR")"

echo -e "${YELLOW}Корневая директория проектов: $PARENT_DIR${NC}"
cd "$PARENT_DIR"

# Функция для клонирования или обновления репозитория
clone_or_update_repo() {
    local repo_url="$1"
    local repo_name="$2"
    
    echo -e "${YELLOW}Работаем с репозиторием: $repo_name${NC}"
    
    if [ -d "$repo_name" ]; then
        echo "Репозиторий уже существует. Обновляем..."
        cd "$repo_name"
        
        # Проверяем, является ли директория git репозиторием
        if [ -d ".git" ]; then
            # Получаем имя текущей ветки
            current_branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown")
            echo "Текущая ветка: $current_branch"
            
            # Пробуем обновить текущую ветку
            if git pull origin "$current_branch" 2>/dev/null; then
                echo "Репозиторий обновлен (ветка: $current_branch)"
            elif git pull origin main 2>/dev/null; then
                echo "Репозиторий обновлен (ветка: main)"
            elif git pull origin master 2>/dev/null; then
                echo "Репозиторий обновлен (ветка: master)"
            else
                echo -e "${YELLOW}Не удалось обновить репозиторий автоматически${NC}"
                echo "Продолжаем с текущим состоянием..."
            fi
        else
            echo -e "${RED}Ошибка: Директория $repo_name не является git репозиторием${NC}"
            exit 1
        fi
        
        cd ..
    else
        echo "Клонируем репозиторий..."
        git clone "$repo_url" "$repo_name"
        
        if [ $? -eq 0 ]; then
            echo "Репозиторий успешно клонирован"
        else
            echo -e "${RED}Ошибка при клонировании $repo_name${NC}"
            exit 1
        fi
    fi
}

# Функция для создания .env файла если его нет
create_env_if_missing() {
    local env_file=".env"
    
    if [ ! -f "$env_file" ]; then
        echo -e "${YELLOW}Создаем отсутствующий $env_file файл...${NC}"
        
        # Создаем только ваши флаги + минимальные настройки
        cat > "$env_file" << 'EOF'
# Feature flags - все false по умолчанию
DISH_NOT_FOUND="false"
RESETTING_SORTS="false"
TOTAL_COST_NOT_UPDATE="false"
DISH_COUNT_NOT_UPDATE="false"
PHOTO_NOT_DELETE="false"
FOR_NO_REASON="false"
ERROR_ADD_NEW_DISH_INTO_ORDER="false"
ALWAYS_SUCCESS_ABOUT_US="false"

# user-service
ENABLE_REFRESH_SESSION="true"
ENABLE_STAFF_AUTH_VIA_PHONE_NUMBER="false"
ENABLE_CHANGE_PASSWORD="true"
ENABLE_BAD_STATUS_INSTEAD_OF_SUCCESS="false"
ENABLE_SAVE_EDITED_USER="true"

ENABLE_INTERNAL_SERVER_ERROR="false"
ENABLE_SAVE_NULLABLE_PROPS="false"
ENABLE_RETURN_EMPTY_RESULT="false"
ENABLE_MIXED_UP_TOKENS="false"
ENABLE_EXPIRED_ACCESS_TOKEN="false"
ENABLE_OPERATORS_WITH_CLIENTS_BUG="false"

ENABLE_SAVE_PASSWORD_BUG="false"
ENABLE_SET_ADMIN_ROLE_TO_CLIENT_BUG="false"
ENABLE_SET_CLIENT_ROLE_TO_OPERATOR_BUG="false"
ENABLE_OK_STATUS_BUG="false"
ENABLE_SAVE_REFRESH_BUG="false"

# menu
BUG_WRONG_PRICE_SORTING="false"
BUG_CORRUPT_PHOTOS_PATHS="false"
BUG_DUPLICATE_RATING_SAVE="false"
BUG_CATEGORY_DELETE_ALLOWED="false"
BUG_FOOD_DETAILS_WRONG_USER_RATING="false"

BUG_AVG_RATING_INCORRECT="false"
BUG_PARTIAL_SAVE="false"
BUG_WRONG_USER_DATA="false"
BUG_DELETED_FOOD_VISIBLE="false"
BUG_FOOD_UPDATE_NOT_SAVED="false"

BUG_ALLOW_RATING_WITHOUT_ORDER="false"
BUG_IGNORE_ONE_PRICE_BOUND="false"
BUG_DROP_FOOD_PHOTOS_ON_READ="false"
BUG_NAME_SEARCH_CASE_SENSITIVE="false"
BUG_DISTORT_FOOD_PRICE_ON_DETAILS="false"
BUG_SEARCH_EXACT_NAME_ONLY="false"

# orderservice
BUG_WRONG_STATISTIC_COUNTER="false"
BUG_WRONG_NUMBER_COUNTER="false"
BUG_WRONG_STATUS_CHANGE="false"
BUG_OPERATOR_WITHOUT_FULL_NAME="false"
BUG_STATUS_HISTORY_NOT_CHANGES="false"

# cart-service
BUG_INVALID_PRICE_COUNT_AFTER_DISH_ADD_IN_ORDER="false"
BUG_INVALID_PRICE_COUNT_AFTER_DISH_DELETE_FROM_ORDER="false"
BUG_CANT_GET_DISH_INFORMATION="false"
BUG_ERROR_STATUS_CHANGE_WHEN_DECLINE_ORDER="false"
BUG_NOT_INCREASE_DISH_AMOUNT_AFTER_ADD="false"
EOF
        
        echo -e "${GREEN}Файл $env_file создан${NC}"
        echo -e "${YELLOW}Все feature flags установлены в false${NC}"
    else
        echo -e "${GREEN}Файл $env_file уже существует${NC}"
    fi
}

# 1. Скачиваем/обновляем все проекты
echo -e "\n${GREEN}1. Скачивание/обновление репозиториев${NC}"

clone_or_update_repo "https://github.com/AsmrS4/hits-food-auth-service" "hits-food-auth-service"
clone_or_update_repo "https://github.com/rkadl77/FoodService" "FoodService"
clone_or_update_repo "https://github.com/egorycheva28/delivery-website" "delivery-website"
clone_or_update_repo "https://github.com/Viroos973/delivery-website-operator" "delivery-website-operator"

# 2. Сборка Docker образов
echo -e "\n${GREEN}2. Сборка Docker образов${NC}"

# 2.1. Сборка cart-image из FoodService
echo -e "\n${YELLOW}Сборка cart-image из FoodService${NC}"
cd "FoodService"
echo "Текущая директория: $(pwd)"
echo "Запускаем docker build..."
docker build -t cart-image:latest .
cd ..

# 2.2. Сборка front-client из delivery-website
echo -e "\n${YELLOW}Сборка front-client из delivery-website${NC}"
cd "delivery-website"
echo "Текущая директория: $(pwd)"
echo "Запускаем docker build..."
docker build -t front-client:latest .
cd ..

# 2.3. Сборка front-operator из delivery-website-operator
echo -e "\n${YELLOW}Сборка front-operator из delivery-website-operator${NC}"
cd "delivery-website-operator"
echo "Текущая директория: $(pwd)"
echo "Запускаем docker build..."
docker build -t front-operator:latest .
cd ..

# 3. Сборка микросервисов из hits-food-auth-service
echo -e "\n${GREEN}3. Сборка микросервисов из hits-food-auth-service${NC}"
cd "hits-food-auth-service"
echo "Текущая директория: $(pwd)"

create_env_if_missing

# Проверяем существование файлов
echo "Проверяем существование Dockerfile..."
if [ ! -f "user-service/Dockerfile" ]; then
    echo -e "${RED}Ошибка: Не найден user-service/Dockerfile${NC}"
    exit 1
fi
if [ ! -f "menu/Dockerfile" ]; then
    echo -e "${RED}Ошибка: Не найден menu/Dockerfile${NC}"
    exit 1
fi
if [ ! -f "orderservice/Dockerfile" ]; then
    echo -e "${RED}Ошибка: Не найден orderservice/Dockerfile${NC}"
    exit 1
fi

# Сборка микросервисов
echo -e "\n${YELLOW}Сборка user-image${NC}"
docker build -f user-service/Dockerfile -t user-image:latest .

echo -e "\n${YELLOW}Сборка menu-image${NC}"
docker build -f menu/Dockerfile -t menu-image:latest .

echo -e "\n${YELLOW}Сборка order-image${NC}"
docker build -f orderservice/Dockerfile -t order-image:latest .

echo -e "\n${YELLOW}Сборка log-image${NC}"
docker build -f log-service/Dockerfile -t log-image:latest .

# 4. Запуск docker-compose
echo -e "\n${GREEN}4. Запуск docker-compose${NC}"

# Проверяем наличие docker-compose.yml
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}Ошибка: Не найден docker-compose.yml${NC}"
    echo "Создайте docker-compose.yml в корне проекта hits-food-auth-service"
    exit 1
fi

echo "Запускаем docker-compose в фоновом режиме..."
docker compose up -d

echo -e "\n${GREEN}Контейнеры запущены в фоновом режиме${NC}"
echo -e "${GREEN}Сайт клиента доступен на http://localhost:4173${NC}"
echo -e "${GREEN}Сайт оператора доступен на http://localhost:4175${NC}"
echo -e "${YELLOW}Для просмотра логов: docker compose logs -f${NC}"
echo -e "${YELLOW}Для остановки: docker compose down${NC}"
echo -e "\n${GREEN}=== Сборка завершена ===${NC}"
