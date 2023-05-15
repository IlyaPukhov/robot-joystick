#include <L298NX2.h>
#include <Ultrasonic.h>

#define PIN_ENA 3  // Вывод управления скоростью вращения мотора №1
#define PIN_ENB 2  // Вывод управления скоростью вращения мотора №2
#define PIN_IN1 7  // Вывод управления направлением вращения мотора №1
#define PIN_IN2 6  // Вывод управления направлением вращения мотора №1
#define PIN_IN3 5  // Вывод управления направлением вращения мотора №2
#define PIN_IN4 4  // Вывод управления направлением вращения мотора №2
int tonePin = 11;
int trigPin = 9;
int echoPin = 10;

long duration, distance;
int val;


void setup() {
  // Установка всех управляющих пинов в режим выхода
  pinMode(PIN_ENA, OUTPUT);
  pinMode(PIN_ENB, OUTPUT);
  pinMode(PIN_IN1, OUTPUT);
  pinMode(PIN_IN2, OUTPUT);
  pinMode(PIN_IN3, OUTPUT);
  pinMode(PIN_IN4, OUTPUT);

  // Команда остановки двум моторам
  digitalWrite(PIN_IN1, LOW);
  digitalWrite(PIN_IN2, LOW);
  digitalWrite(PIN_IN3, LOW);
  digitalWrite(PIN_IN4, LOW);

  // Установка для сонара
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  // Установка для пьезоэлемента
  pinMode(tonePin, OUTPUT);

  Serial.begin(9600);
}

void loop() {

  // Управление моторами и командами направления движения
  if (Serial.available() > 0) {
    val = Serial.read();

    // Команда вперед
    if (val == 'f') {
      analogWrite(PIN_ENA, 255);  // Устанавливаем скорость 1-го мотора
      analogWrite(PIN_ENB, 255);  // Устанавливаем скорость 2-го мотора

      // Задаём направление для 1-го мотора
      digitalWrite(PIN_IN1, HIGH);
      digitalWrite(PIN_IN2, LOW);

      // Задаём направление для 2-го мотора
      digitalWrite(PIN_IN3, HIGH);
      digitalWrite(PIN_IN4, LOW);
    }

    // Команда назад
    if (val == 'b') {
      analogWrite(PIN_ENA, 255);  // Устанавливаем скорость 1-го мотора
      analogWrite(PIN_ENB, 255);  // Устанавливаем скорость 2-го мотора

      // Задаём направление для 1-го мотора
      digitalWrite(PIN_IN1, LOW);
      digitalWrite(PIN_IN2, HIGH);

      // Задаём направление для 2-го мотора
      digitalWrite(PIN_IN3, LOW);
      digitalWrite(PIN_IN4, HIGH);
    }

    // Команда поворота направо
    if (val == 'r') {
      analogWrite(PIN_ENA, 150);  // Устанавливаем скорость 1-го мотора

      // Задаём направление для 1-го мотора
      digitalWrite(PIN_IN1, HIGH);
      digitalWrite(PIN_IN2, LOW);

      // остановка 2-го мотора
      digitalWrite(PIN_IN3, LOW);
      digitalWrite(PIN_IN4, LOW);

      delay(600);

      // остановка 1-го мотора
      digitalWrite(PIN_IN1, LOW);
      digitalWrite(PIN_IN2, LOW);
    }

    // Команда поворота налево
    if (val == 'l') {
      analogWrite(PIN_ENB, 150);  // Устанавливаем скорость 2-го мотора

      // остановка для 1-го мотора
      digitalWrite(PIN_IN1, LOW);
      digitalWrite(PIN_IN2, LOW);

      // Задаём направление для 2-го мотора
      digitalWrite(PIN_IN3, HIGH);
      digitalWrite(PIN_IN4, LOW);

      delay(600);

      // остановки 2-го мотора
      digitalWrite(PIN_IN3, LOW);
      digitalWrite(PIN_IN4, LOW);
    }

    // Команда стоп
    if (val == 's') {
      // Команда остановки двум моторам
      digitalWrite(PIN_IN1, LOW);
      digitalWrite(PIN_IN2, LOW);
      digitalWrite(PIN_IN3, LOW);
      digitalWrite(PIN_IN4, LOW);
    }
  }

  // Работа сонара
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  duration = pulseIn(echoPin, HIGH);
  distance = duration / 58.2;
  delay(10);

  // Работа пьезоэлемента при препятствии
  if (distance < 18) {
    tone(tonePin, 2000);
    delay(500);
  }
  if (distance > 19) {
    noTone(tonePin);
  }
}