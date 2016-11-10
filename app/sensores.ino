/* Sensores
  ------------------
  Laboratorio IoT para Arquitectura de Software 2016-20
  Universidad de los Andes
  
  el ejemplo lee una entrada analoga de los sensores y
  la transmite por el puerto serial como un string cada segundo.

  creado el 18 de julio de 2016
*/
#include <string.h>
// Selecciona el pin de entrada analoga a leer (temperatura).
int tempPin = 5;
// Selecciona el pin de entrada analoga a leer (caudal).
int caudalPin = 4;
// Selecciona el pin de entrada analoga a leer (energía).
int energiaPin = 3;
// Selecciona el pin de entrada analoga a leer (emergencia).
int emergenciaPin = 2
                    // variable para guardar el valor del sensor de temperatura.
                    int tempC = 0;
// variable para guardar el valor del sensor de caudal.
int caudalC = 0;
// variable para guardar el valor del sensor de energía.
int energiaC = 0;
// variable para guardar el valor del sensor de emergencia.
int emergenciaC = 0;
// variable para la unidad de temperatura C-Celsius, K- Kelvin, F-Fahrenheit.
char tempUnit = 'C';
// variable para la unidad de caudal litros por minuto
char caudalUnit = 'L/m';
// variable para la unidad de energía kWh.
char energiaUnit = 'kWh';
// arreglo de chars para envio final del dato del sensor.
String tempArray[2] = {"", ""};
// arreglo de chars para envio final del dato del sensor.
String caudalArray[2] = {"", ""};
// arreglo de chars para envio final del dato del sensor.
String energiaArray[2] = {"", ""};
// arreglo de chars para envio final del dato del sensor.
String emergenciaArray[1] = {""};
// variable temporal de conteo
int i = 0;
// preparacion del proceso
void setup() {
  // Abre puerto serial y lo configura a 9600 bps
  Serial.begin(9600);
  // se fija la unidad de trabajo del sensor de temperatura.
  tempArray[1] = String(tempUnit);
  caudalArray[1] = String(caudalUnit);
  energiaArray[1] = String(energiaUnit);
  // ejecuta el procesamiento del sensor
  void loop() {
    // lee el valor del sensor de temperatura en Volts
    tempC = analogRead(tempPin);
    // lee el valor del sensor de caudal en Volts
    caudalC = analogRead(caudalPin);
    // lee el valor del sensor de energía en Volts
    energiaC = analogRead(energiaPin);
    // lee el valor del sensor de emergencia en Volts
    emergenciaC = digitalRead(emergenciaPin);
    // Convierte el valor de temperatura a grados centigrados y de analogo a digital
    // DEPENDE DEL SENSOR, REVISAR DATASHEET
    tempC = (4.9 * tempC * 100.0) / 1024.0;
    // Convierte el valor de caudal a m^3/s y de analogo a digital
    // DEPENDE DEL SENSOR, REVISAR DATASHEET
    caudalC = (cuadalC * 60.0) / 350.0;
    // Convierte el valor de energía a kWh y de analogo a digital
    // DEPENDE DEL SENSOR, REVISAR DATASHEET
    energiaC = (4.9 * energiaC * 100.0) / 1024.0 - 230;
    //CODIGO DE EMERGENCIA
    if (emergenciaC == HIGH) {
      emergenciaC = 1;
    } else {
      emergenciaC = 0;
    }
    // se transforma el dato int de temperatura a un char
    tempArray[0] = String(tempC);
    // se transforma el dato int de caudal a un char
    caudalArray[0] = String(caudalC);
    // se transforma el dato int de energia a un char
    energiaArray[0] = String(energiaC);
    //CÓDIGO DE EMERGENCIA
    emergenciaArray[0] = String(emergenciaC);


    // Envia los datos uno por uno del arreglo del sensor por puerto serial
    for (i = 0; i < 2; i++) {
      // imprime el elemento del arreglo por el puerto serial
      Serial.print(tempArray[i]);
      // espacio para diferenciar elementos en el arreglo
      if (i < 1) {
        Serial.print("\t");
      }
    }
    Serial.print("\n");
    // Envia los datos uno por uno del arreglo del sensor por puerto serial
    for (i = 0; i < 2; i++) {
      // imprime el elemento del arreglo por el puerto serial
      Serial.print(caudalArray[i]);
      // espacio para diferenciar elementos en el arreglo
      if (i < 1) {
        Serial.print("\t");
      }
    }
    Serial.print("\n");
    // Envia los datos uno por uno del arreglo del sensor por puerto serial
    for (i = 0; i < 2; i++) {
      // imprime el elemento del arreglo por el puerto serial
      Serial.print(energiaArray[i]);
      // espacio para diferenciar elementos en el arreglo
      if (i < 1) {
        Serial.print("\t");
      }
    }
    Serial.print("\n");
    Serial.print(emergenciaArray[0]);
    Serial.print("\t");

    // final de la trama de datos
    Serial.println("");
    // espera 1 segundo para repetir el procedimiento
    delay(1000);
  }

