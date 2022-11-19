# Sleep(easy)

## 1. 要求

- Before you start coding, read Chapter 1 of the [xv6 book](https://pdos.csail.mit.edu/6.S081/2020/xv6/book-riscv-rev1.pdf).
- Look at some of the other programs in `user/` (e.g., `user/echo.c`, `user/grep.c`, and `user/rm.c`) to see how you can obtain the command-line arguments passed to a program.
- If the user forgets to pass an argument, sleep should print an error message.
- The command-line argument is passed as a string; you can convert it to an integer using `atoi` (see user/ulib.c).
- Use the system call `sleep`.
- See `kernel/sysproc.c` for the xv6 kernel code that implements the `sleep` system call (look for `sys_sleep`), `user/user.h` for the C definition of `sleep` callable from a user program, and `user/usys.S` for the assembler code that jumps from user code into the kernel for `sleep`.
- Make sure `main` calls `exit()` in order to exit your program.
- Add your `sleep` program to `UPROGS` in Makefile; once you've done that, `make qemu` will compile your program and you'll be able to run it from the xv6 shell.
- Look at Kernighan and Ritchie's book *The C programming language (second edition)* (K&R) to learn about C.

## 2. 文件主架构

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
    
}
```

## 3. 头文件展开

### 3.1 kernel/types.h

```c
typedef unsigned int   uint;
typedef unsigned short ushort;
typedef unsigned char  uchar;

typedef unsigned char uint8;
typedef unsigned short uint16;
typedef unsigned int  uint32;
typedef unsigned long uint64;

typedef uint64 pde_t;
```

### 3.2 kernel/stat.h

```c
#define T_DIR     1   // Directory
#define T_FILE    2   // File
#define T_DEVICE  3   // Device

struct stat {
  int dev;     // File system's disk device
  uint ino;    // Inode number
  short type;  // Type of file
  short nlink; // Number of links to file
  uint64 size; // Size of file in bytes
};
```

### 3.3 user/user.h

```c
struct stat;
struct rtcdate;

// system calls
int fork(void);
int exit(int) __attribute__((noreturn));
int wait(int*);
int pipe(int*);
int write(int, const void*, int);
int read(int, void*, int);
int close(int);
int kill(int);
int exec(char*, char**);
int open(const char*, int);
int mknod(const char*, short, short);
int unlink(const char*);
int fstat(int fd, struct stat*);
int link(const char*, const char*);
int mkdir(const char*);
int chdir(const char*);
int dup(int);
int getpid(void);
char* sbrk(int);
//*********此处有sleep的系统调用************//
int sleep(int); 
int uptime(void);

// ulib.c
int stat(const char*, struct stat*);
char* strcpy(char*, const char*);
void *memmove(void*, const void*, int);
char* strchr(const char*, char c);
int strcmp(const char*, const char*)
void fprintf(int, const char*, ...);
void printf(const char*, ...);
char* gets(char*, int max);
uint strlen(const char*);
void* memset(void*, int, uint);
void* malloc(uint);
void free(void*);
int atoi(const char*);
int memcmp(const void *, const void *, uint);
void *memcpy(void *, const void *, uint);
```

### 4. 程序

在User文件下，创建sleep.c

```c
#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"

int
main(int argc, char *argv[])
{

  if (argc != 2){
  	fprintf(2, "Usage: Sleep Num Seconds\n");
    exit(1);
  }

  int sleep_sec = atoi(argv[1]);
  sleep(sleep_sec);
  exit(0);
}


```

## 5. 测试

### 5.1 退出系统 

输入ctrl + a 松开 再按一下x，退出xv6系统

### 5.2 输入测试脚本

```shell
./grade-lab-util sleep
```

### 5.3 测试结果

![image-20221119235045323](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221119235045323.png)

