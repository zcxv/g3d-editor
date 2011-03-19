@echo off
title G3DEditor
java -Xmx512m -Djava.library.path=./lib/ -cp ./lib/*;G3DEditor.jar g3deditor.Main
pause
