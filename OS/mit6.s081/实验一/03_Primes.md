# Primes

​		Write a concurrent version of prime sieve using pipes. This idea is due to Doug McIlroy, inventor of Unix pipes. The picture halfway down [this page](http://swtch.com/~rsc/thread/) and the surrounding text explain how to do it. Your solution should be in the file `user/primes.c`.

​		Your goal is to use `pipe` and `fork` to set up the pipeline. The first process feeds the numbers 2 through 35 into the pipeline. For each prime number, you will arrange to create one process that reads from its left neighbor over a pipe and writes to its right neighbor over another pipe. Since xv6 has limited number of file descriptors and processes, the first process can stop at 35.

## 1. 提示

- Be careful to close file descriptors that a process doesn't need, because otherwise your program will run xv6 out of resources before the first process reaches 35.
- Once the first process reaches 35, it should wait until the entire pipeline terminates, including all children, grandchildren, &c. Thus the main primes process should only exit after all the output has been printed, and after all the other primes processes have exited.
- Hint: `read` returns zero when the write-side of a pipe is closed.
- It's simplest to directly write 32-bit (4-byte) `int`s to the pipes, rather than using formatted ASCII I/O.
- You should create the processes in the pipeline only as they are needed.
- Add the program to `UPROGS` in Makefile

## 2. 质数过滤的方法

![image-20221127233622308](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221127233622308.png)

从主进程开始，不断新建子进程，每个子进程执行一次筛选，并将使用的基（base）认为是质数（这是正确的，请思考为什么），并返回，直到全部的数都被筛去或被返回。各个进程之间的通讯将会使用到管道。

## 3.代码

```c
// 对数据类型进行定义或者重命名
#include "kernel/types.h"
// 可以返回文件的结构
#include "kernel/stat.h"
// 返回user.h定义的函数，其中含有sleep函数的
#include "user/user.h"

//argc:指的是输入指令的个数
//argv:指的是输入指令的字符串

// 声明prime函数
void prime(int input_df);

int main(int agrc, char *argv[]){
	int parent_fd[2];
	// 初始化管道
	pipe(parent_fd);
	// 创建子进程 父端从2开始往管道的写端写入数据
	if(fork()){
		// 建议关闭管道的读端
		close(parent_fd[0]);
		// 通过for循环向写端写入数据
		for (int i = 2; i < 36; i++){
			write(parent_fd[1], &i, sizeof(i));
		}
        // 用完写端记得关闭
		close(parent_fd[1]);
	}else{
		// 关闭parent_fd的写端
		close(parent_fd[1]);
		// 将读端读到的数据送到primes判别函数里面
		prime(parent_fd[0]);
	}
	// 等待子进程结束
	wait(0);
	// 退出系统
	exit(0);
}

void prime(int input_df){
	int base;
	// 当是最后一个进程时，退出
	if (read(input_df, &base,sizeof(int))==0){
		exit(0);
	}
	printf("prime %d\n", base);
	int p[2];
        pipe(p);
	if (fork()){
		// 关闭读进程，此时要写入数据
		close(p[0]);
		int n;
		int eof;
		do {
			eof = read(input_df,&n,sizeof(int));
			if (n % base != 0){
				write(p[1], &n, sizeof(int));
			}
		}while(eof);
		close(p[1]);
	}else{
		// 子进程关闭写端
		close(p[1]);
		// 将读到的数据再传入
		prime(p[0]);
	}
	// 等待子进程结束
	wait(0);
    // 退出
	exit(0);
}
```



