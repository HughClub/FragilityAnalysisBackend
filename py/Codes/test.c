#include<stdio.h>

void fun(int *a, int n, int *odd, int *even) {
    for(int i = 0; i < n; i++) {
        if(a[i] % 2 == 0) {
            *even += a[i];
        } else {
            *odd += a[i];
        }
    }
}

int main() {
    
    int a[] = {1, 2, 3, 4, 5};
    int n = sizeof(a) / sizeof(a[0]);
    int odd = 0, even = 0;
    fun(a, n, &odd, &even);
    printf("Sum of odd elements: %d\n", odd);
    printf("Sum of even elements: %d\n", even);
    return 0;
}