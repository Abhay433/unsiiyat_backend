#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper Startup Batch script, version 3.3.2
# ----------------------------------------------------------------------------

# Set environment
[ -z "$JAVA_HOME" ] && JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null)"
export JAVA_HOME

# Determine PRG
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASE_DIR=`cd "$PRGDIR" >/dev/null; pwd`

# Locate wrapper jar or maven home
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROP="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

# Check if Maven is already on path
if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

# If wrapper jar exists, run it
if [ -f "$WRAPPER_JAR" ]; then
  exec java -jar "$WRAPPER_JAR" "$@"
fi

# Otherwise, attempt to download wrapper or maven if curl/wget is available
echo "Downloading Maven Wrapper..."
JAR_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
mkdir -p "$BASE_DIR/.mvn/wrapper"

if command -v curl >/dev/null 2>&1; then
  curl -fsSL -o "$WRAPPER_JAR" "$JAR_URL"
elif command -v wget >/dev/null 2>&1; then
  wget -q -O "$WRAPPER_JAR" "$JAR_URL"
fi

if [ -f "$WRAPPER_JAR" ]; then
  exec java -jar "$WRAPPER_JAR" "$@"
else
  echo "Error: Could not find or download maven-wrapper.jar. Please install Maven or run with network access." >&2
  exit 1
fi
