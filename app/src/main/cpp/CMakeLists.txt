cmake_minimum_required(VERSION 3.22.1)

project("tttclient")

# ============================================
# 1. НАСТРОЙКИ C++
# ============================================

# Включаем C++20 (нужен для корутин)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ============================================
# 2. ПОДКЛЮЧЕНИЕ BOOST
# ============================================


# Путь к папке с библиотеками Boost
set(BOOST_PATH ${CMAKE_CURRENT_SOURCE_DIR})


# ============================================
# 3. ДОБАВЛЕНИЕ СТАТИЧЕСКИХ БИБЛИОТЕК BOOST
# ============================================

# Функция для упрощения добавления библиотек
function(add_boost_library LIB_NAME)
    add_library(${LIB_NAME} STATIC IMPORTED)

    # Явное указание суффиксов для каждой архитектуры
    if (${ANDROID_ABI} STREQUAL "x86_64")
        set(SUFFIX "x64")
    elseif(${ANDROID_ABI} STREQUAL "arm64-v8a")
        set(SUFFIX "a64")
    elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
        set(SUFFIX "arm")
    elseif(${ANDROID_ABI} STREQUAL "x86")
        set(SUFFIX "x86")
    else()
        set(SUFFIX ${ANDROID_ABI})
    endif()

    # Ищем файлы с точным соответствием паттерну
    set(RELEASE_FILE "${BOOST_PATH}/third_party/${ANDROID_ABI}/lib/lib${LIB_NAME}-${ANDROID_ABI}-${SUFFIX}.a")
    set(DEBUG_FILE "${BOOST_PATH}/third_party/${ANDROID_ABI}/lib/lib${LIB_NAME}-${ANDROID_ABI}-d-${SUFFIX}.a")

    # Проверяем существование файлов (опционально)
    if(NOT EXISTS ${RELEASE_FILE})
        message(WARNING "Release библиотека не найдена: ${RELEASE_FILE}")
    endif()
    if(NOT EXISTS ${DEBUG_FILE})
        message(WARNING "Debug библиотека не найдена: ${DEBUG_FILE}")
    endif()

    set_target_properties(${LIB_NAME} PROPERTIES
            IMPORTED_LOCATION_RELEASE ${RELEASE_FILE}
            IMPORTED_LOCATION_DEBUG ${DEBUG_FILE}
            IMPORTED_LOCATION ${RELEASE_FILE}
    )
endfunction()

# ============================================
# 3.1 ОБЯЗАТЕЛЬНЫЕ БИБЛИОТЕКИ
# ============================================

#add_boost_library(boost_system) не нужна с версии 1.69 буста, является header only
# 3.2 ОПЦИОНАЛЬНЫЕ БИБЛИОТЕКИ
# ============================================

# Boost.Filesystem - если работаете с файлами
add_boost_library(boost_filesystem)

# Boost.Thread - если используете boost::thread вместо std::thread
add_boost_library(boost_thread)

# Boost.Atomic - может потребоваться для некоторых операций
add_boost_library(boost_atomic)

# ============================================
# 4. ВАША ОСНОВНАЯ NATIVE-БИБЛИОТЕКА
# ============================================

# Укажите здесь ваши .cpp файлы


add_library(${CMAKE_PROJECT_NAME} SHARED
        src/native-lib.cpp
        src/connection/TTTClientSession.cpp
        src/JNICallback/JNICallback.cpp
        src/JNICallback/JNIClassLoader.cpp
        src/connection/TTTSessionManager.cpp

)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        "${BOOST_PATH}/third_party/boost_1_91_0"                     # Для #include <boost/...>
        "${CMAKE_CURRENT_SOURCE_DIR}" # Для ваших заголовков
        "${CMAKE_CURRENT_SOURCE_DIR}/src/connection"
        "${CMAKE_CURRENT_SOURCE_DIR}/src/JNICallback"
        "${CMAKE_CURRENT_SOURCE_DIR}/third_party/nlohmann/include"
)

# ============================================
# 5. ЛИНКОВКА БИБЛИОТЕК
# ============================================

# Библиотеки Android (логи, система)
find_library(log-lib log)

# Линкуем все Boost библиотеки
target_link_libraries(${CMAKE_PROJECT_NAME}
        #boost_system          # Обязательно
        boost_filesystem      # Раскомментируйте если добавили
        boost_thread          # Раскомментируйте если добавили
        boost_atomic          # Раскомментируйте если добавили
        android          # Стандартная библиотека логов Android
        log
)

# ============================================
# 6. ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ
# ============================================

# Если используете исключения (рекомендуется для Asio)
target_compile_options(${CMAKE_PROJECT_NAME} PRIVATE
        -fexceptions
        -frtti
)

# Для обработки ошибок с системными вызовами
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        BOOST_ASIO_DISABLE_THREAD_KEYWORD_EXTENSION
        BOOST_ASIO_NO_DEPRECATED
)

# ============================================
# 7. ИНКЛЮДЫ ДЛЯ ОТЛАДКИ (опционально)
# ============================================

message(STATUS "ANDROID_ABI: ${ANDROID_ABI}")
message(STATUS "Boost path: ${BOOST_PATH}")
message(STATUS "Boost sources: ${NATIVE_SOURCES}")