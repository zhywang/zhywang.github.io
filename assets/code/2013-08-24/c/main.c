#include <stdio.h>
#include "stack.h"
int main(int argc, char* argv[]) {
	int i=0;
	Stack s=create();
	while(i<10 && push(i, s))
		i++;
	while(!is_empty(s) && pop(&i, s))
		printf("%d\n",i);
}
