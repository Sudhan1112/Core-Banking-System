#!/bin/bash
set -e

MAVEN_VERSION="3.9.6"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
INSTALL_DIR="$(pwd)/.mvn-portable"
ZIP_FILE="$(pwd)/maven.zip"

if [ -d "$INSTALL_DIR" ]; then
    echo "Maven already installed in $INSTALL_DIR"
    exit 0
fi

echo "Downloading Maven $MAVEN_VERSION..."
curl -L -o "$ZIP_FILE" "$MAVEN_URL"

echo "Extracting Maven..."
unzip -q "$ZIP_FILE"
mv "apache-maven-$MAVEN_VERSION" "$INSTALL_DIR"
rm "$ZIP_FILE"

echo "Maven installed to $INSTALL_DIR"
echo "You can verify it by running: .mvn-portable/bin/mvn -version"
