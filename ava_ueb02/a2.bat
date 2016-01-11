:: Aufrufen der Knoten für A2																 
set leader=1
set follower=0
start "Node 1 (Leader)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 1 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 2 (Leader)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 2 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 3 (Leader)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 3 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 4 (Leader)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 4 -hosts hostlist.json -exercise 2 exercise_config.json

start "Node 5 (Follower)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 5 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 6 (Follower)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 6 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 7 (Follower)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 7 -hosts hostlist.json -exercise 2 exercise_config.json
start "Node 8 (Follower)" java -jar C:\Users\cgeidt\AVA\ava_ueb02\out\artifacts\ava_ueb01_jar\ava_ueb01.jar -id 8 -hosts hostlist.json -exercise 2 exercise_config.json