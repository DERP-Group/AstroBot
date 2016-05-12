#!/bin/bash

#1. Set variables
project_name="AstroBot"
lowercase_name="astrobot"
astrobot_directory=$(pwd)

#2. Make new dir and copy astrobot into it
cd ..
if [ -d "$project_name" ]; then
  ECHO "PROJECT DIRECTORY EXISTS - EXITING"
  exit
fi
 
mkdir $project_name
cp -r $astrobot_directory/. $project_name

cd $project_name

#3. remove things that shouldn't be cloned
rm -rf .git
rm -rf voice-interface
rm -rf ./service/target

#4. Rename files
mv ./astrobot.json ./$lowercase_name".json"
mv ./astrobot_local.json ./$lowercase_name"_local.json"
mv ./service/src/main/java/com/derpgroup/astrobot ./service/src/main/java/com/derpgroup/$lowercase_name

#5. Rename values for maven and eclipse

#6. Rename packages
find . -type f -exec sed -i "s/astrobot/"$lowercase_name"/g" {} +
find . -type f -exec sed -i "s/AstroBot/"$project_name"/g" {} +