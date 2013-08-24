#ifndef _STACK_H
#define _STACK_H

typedef struct stack{
	int *data;
	int capacity;
	int index;
} *Stack;

Stack create();
int is_empty(Stack);
int pop(int*, Stack);
int push(int, Stack);

#endif
