//
//  SomeNew.c
//  iosApp
//
//  Created by Greg Zenkov on 12/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

#include "SomeNew.h"

struct Pointer {
    int x;
    int y;
};

Pointer *create_pointer(double width, double height) {
    Pointer *p = malloc(sizeof(struct Pointer));
    if (p) {
        p->x = randomX(width);
        p->y = randomY(height);
    }
    return p;
}

double pointer_x(Pointer *p) {
    return p->x;
}

double pointer_y(Pointer *p) {
    return p->y;
}

double randfrom(double min, double max){
    double range = (max - min);
    double div = RAND_MAX / range;
    return min + (rand() / div);
}

double randomX(double width) {
    double offsetX = width / 6;
    double randomX = randfrom(-offsetX, offsetX);
    return randomX;
}

double randomY(double height) {
    double offsetY = height / 6;
    double randomY = randfrom(-offsetY, offsetY);
    return randomY;
}
