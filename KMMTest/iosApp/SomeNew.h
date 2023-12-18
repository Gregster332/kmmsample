//
//  SomeNew.h
//  iosApp
//
//  Created by Greg Zenkov on 12/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

#ifndef SomeNew_h
#define SomeNew_h

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef struct Pointer Pointer;
Pointer *create_pointer(double width, double height);
double pointer_x(Pointer *p);
double pointer_y(Pointer *p);
double randfrom(double min, double max);
double randomX(double width);
double randomY(double height);

#endif /* SomeNew_h */
