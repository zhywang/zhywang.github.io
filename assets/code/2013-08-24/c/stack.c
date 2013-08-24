#include "stack.h"
#include <stdlib.h>

#define INCREASE_SIZE 8
int ensureCapacity(Stack stack) {
    if(stack->index + 1 < stack->capacity)
        return 1;
	int* temp= (int*)realloc(stack->data, sizeof(int)*(stack->capacity + INCREASE_SIZE));
    if(temp == NULL){
        return 0;
    }
	stack->data=temp;
	stack->capacity+=INCREASE_SIZE;
    return 1;
}

Stack create() {
	Stack stack=(Stack)malloc(sizeof(struct stack));
	if(stack) {
		stack->data=NULL;
		stack->index=-1;
		stack->capacity=0;
	}
	return stack;
}

int is_empty(Stack stack) {
    return stack->index == -1;
}

int pop(int *elem, Stack stack) {
	if(is_empty(stack))
        return 0;
	*elem = stack->data[stack->index];
	stack->index--;
	return 1;
}

int push(int value, Stack stack) {
		if(!ensureCapacity(stack))
			return 0;
	stack->data[++(stack->index)]=value;
	return 1;
}
