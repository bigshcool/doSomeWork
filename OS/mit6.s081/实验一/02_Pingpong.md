# pingpong(easy)

## 1. 要求

- Use `pipe` to create a pipe.
- Use `fork` to create a child.
- Use `read` to read from the pipe, and `write` to write to the pipe.
- Use `getpid` to find the process ID of the calling process.
- Add the program to `UPROGS` in Makefile.
- User programs on xv6 have a limited set of library functions available to them. You can see the list in `user/user.h`; the source (other than for system calls) is in `user/ulib.c`, `user/printf.c`, and `user/umalloc.c`.

## 2. 思路

- 本题类似于**父子进程利用管道进行通信**

- 需要使用到user.h里面的函数以及其他的系统调用
- 注意**需要在创建管道以后再创建进程，不然无法正常输出**
- 需要再user文件下创建文件，然后在Makefile的`UPROGS`上添加信息

## 3. 程序展开

```c
// 对数据类型进行定义或者重命名
#include "kernel/types.h"
// 可以返回文件的结构
#include "kernel/stat.h"
// 返回user.h定义的函数，其中含有sleep函数的
#include "user/user.h"
/**
argc:指的是输入指令的个数
argv:指的是输入指令的字符串
*/
int main(int agrc, char *argv[]){
        int p[2];
        char buf = 'c';
        if(pipe(p) == -1){
                fprintf(2, "init pipe Error");
        }
	    int pid = fork();
        if (pid == -1){
                fprintf(2, "create child process");
                exit(1);
        }
        if (pid == 0){
                read(p[0], &buf, 1);
                pid = getpid();
                printf("%d: received ping\n", pid);
                write(p[1], &buf, 1);
        }else{
                write(p[1], &buf, 1);
                // if we do not sleep here, the parent would read from the pipe, and the child would not be able to read from the pipe
                // if you do not want to sleep here, you should use two pipes instead of one
                sleep(15);
                read(p[0], &buf, 1);
                pid = getpid();
                printf("%d: received pong\n", pid);
        }
        exit(0);
}
```

