#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

# UTF-8 is required: the localisation tables and UI strings contain Khmer text.
JAVAC="javac -encoding UTF-8 -d bin"

case "${1:-run}" in
  test)
    echo "=== World of Wonder — building ==="
    mkdir -p bin
    $JAVAC $(find src -name "*.java")
    echo "=== World of Wonder — regression suite ==="
    java -cp bin com.worldofwonder.test.SmokeTest
    ;;
  *)
    echo "=== World of Wonder (MVC Game) ==="
    mkdir -p bin
    $JAVAC $(find src -name "*.java")
    java -cp bin com.worldofwonder.Main
    ;;
esac
