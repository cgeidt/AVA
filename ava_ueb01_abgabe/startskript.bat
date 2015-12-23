:: Variablen aus Aufgabenstellung 
set n=6
set m=10
set c=3

:: Erzuegen eines Graphen mit Hilfe mit dem GraphGen Programm
java -jar C:\Users\cgeidt\AVA\GraphGen\dist\GraphGen.jar -nodes %n% -edges %m% -output "C:\Users\cgeidt\Desktop\AVA\nodegraph"

:: Erstellen einer Bilddatei des erzuegten Graphen
dot -Tpng nodegraph -o nodegraph.png

:: Öffnen der erzeugten Bilddatei
nodegraph.png

:: Starten der einzelnen Nodeinstanzen
for /l %%x in (1, 1, %n%) do (
   echo %%x
   start "Node %%x" java -jar C:\Users\cgeidt\AVA\ava_ueb01\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id %%x -hosts hostlist.json -graph nodegraph -rumor %c%   
)




