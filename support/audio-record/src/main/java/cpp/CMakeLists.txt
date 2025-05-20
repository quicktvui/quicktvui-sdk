
cmake_minimum_required(VERSION 3.4.1)

#设置变量SRC_DIR为lamemp3的所在路径
set(SRC_DIR lamemp3)

#指定头文件所在，可以多次调用，指定多个路径
include_directories(lamemp3)

#添加自自定义的so库时，有两种方式，一种添加一个目录，一种一个个文件添加

#设定一个目录
aux_source_directory(lamemp3 SRC_LIST)

#将前面目录下所有的文件都添加进去
add_library(lamemp3 SHARED AndroidMP3Encoder.cpp ${SRC_LIST})

find_library(log-lib log)
