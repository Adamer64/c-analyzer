#include <stdio.h>
#include <math.h>
int wrong_input(double inpt){
    int trigger1=0;
    double nominal[13]={100,50,20,10,5,2,1,0.50,0.20,0.1,0.05,0.02,0.01};
     
    for (int i=0; i<13; i++){
        if ( nominal[ i] == inpt){
            trigger1=1;
            break;
        }
    }
     
    if( trigger1!=1 ) return 1;    //chybny vstup  -wrong_input=1
    else return 0;                 //spravny vstup  -wrong_input=0
}
int is_int(double otpt){
    if (floorf(otpt) == otpt) return 1;         //cislo je int  -is_int=1
    else return 0;                              //              -is_int=0
}
void print_money(int z, double cash1){
    if (is_int(cash1) == 1){                        //ak je integer
        if (z == 0) printf ("%.0lf", cash1);         //bez zalomenia
        else if (z == 1) printf ("%.0lf\n", cash1);  //so zalomenim
        }
    else{                                           //neni integer
        if (z == 0) printf ("%.2lf", cash1);         //bez zalomenia
        else if (z == 1) printf ("%.2lf\n", cash1);  //so zalomenim
        }
}
void pay_back(double cash2){
    int j;
    double nominal[13]={100,50,20,10,5,2,1,0.50,0.20,0.1,0.05,0.02,0.01};
    int k=0;
    while (((cash2 / nominal[ k]) < 1 ) && ( k<13)) k++;
    for (; k<13; k++){
        for (j=0; j < floorf(cash2 / nominal[ k]); j++){
                printf (" ");
                print_money(0, nominal[ k]);         
        }
        cash2=cash2-j * nominal[ k];
    }
}
int main(){
double value, input, payment=0, rtrn;
printf ("Enter value of your bill: "); //nacitaj hodnotu nakupu
scanf ("%lf", &value);
    if ((value > 9999.99) || (value < 0.01)){ //najnizsia/najvyssia mozna suma
        printf ("Wrong input!\n");
        return 1;
    }
printf ("Insert money for payment: "); //vloz bankovky a mince
while (1){
    scanf ("%lf",&input);
        if ((wrong_input(input) == 1) && (input != 0)){
            print_money(0,input); printf (" is invalid!\n");
            return 1;
        }
        payment=payment+input; 
        if (input == 0 || getchar() == EOF) break;
          
}
printf ("You have inserted: "); print_money(1,payment); //vypise hodnotu vlozenych minci
rtrn=payment-value;
    if (rtrn<0){
        printf ("Not enough money!\n");
        goto end;
    }
         
printf ("To return: "); print_money(1,rtrn);    //na vratenie
if (rtrn != 0){                                 //ked je co vracat;
    printf ("Collect your payback:");
    pay_back(rtrn);
    printf ("\n");
}
end:
return 0;
}