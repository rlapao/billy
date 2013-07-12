#! /bin/bash

echo "Changing to branch master......"
git checkout master

echo "creating a temporary directory......."
mkdir /tmp/DirAux

echo "Generating javadoc for all projects....."
mvn install
mvn javadoc:aggregate

cp -r target/site/apidocs/. /tmp/DirAux
mvn clean

echo "changing to branch gh-pages....."
git checkout gh-pages

echo "Copying Javadoc to Javadoc folder from the temporary folder....."
cp -r  /tmp/DirAux/. Javadoc/

echo "Starting to push the Javadoc to Git....."
git add Javadoc
git commit -a
git push origin gh-pages
echo "====================================================================================================================="
echo "SUCESS the Javadoc of Billy is now updated!"

