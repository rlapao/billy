#! /bin/bash
echo "Changing to branch master"
git checkout master

echo "creating a temporary directory"
mkdir /tmp/DirAux

echo "Generating javadoc for all projects"
mvn javadoc:aggregate
cp /target/site/apidocs/. /tmp/DirAux
mvn clean

echo "changing to branch gh-pages"
git checkout gh-pages

echo "Copying Javadoc to Javadoc folder from the temporary folder"
cp /tmp/DirAux/. Javadoc/ -r


