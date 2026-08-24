# Slot Machine

## Descripción

Este proyecto consiste en el desarrollo de una máquina tragamonedas virtual utilizando Java y programación orientada a objetos.

La máquina está compuesta por diferentes ruedas (`Wheel`) que contienen símbolos representados mediante colores. El usuario puede agregar y eliminar ruedas y símbolos, posicionar símbolos específicos, girar una rueda individual o todas las ruedas y consultar la configuración actual de la máquina.

El proyecto también incluye una representación gráfica utilizando las clases `Rectangle`, `Circle` y `Canvas`, permitiendo visualizar las ruedas, el marco de la máquina y un indicador que cambia de estado cuando se alcanza un jackpot.

## Funcionalidades principales

- Agregar y eliminar ruedas.
- Agregar y eliminar símbolos de colores.
- Evitar símbolos repetidos dentro de una misma rueda.
- Posicionar una rueda en un símbolo específico.
- Girar una rueda individual o todas las ruedas.
- Consultar los símbolos y la configuración actual.
- Detectar el estado de jackpot.
- Mostrar visualmente el estado de la máquina.
- Informar errores mediante `JOptionPane` cuando corresponde.

## Tecnologías

- Java
- Java Swing
- BlueJ
- Programación Orientada a Objetos

## Estructura principal

- `SlotMachine`: controla la máquina y coordina sus componentes.
- `Wheel`: representa cada rueda y administra sus símbolos.
- `Rectangle`: proporciona la representación gráfica de las ruedas y el marco.
- `Circle`: representa el indicador de jackpot.
- `Canvas`: administra la ventana y el dibujo de los elementos gráficos.

## Objetivo

El objetivo del proyecto es aplicar principios de programación orientada a objetos mediante el diseño e implementación de una máquina tragamonedas, trabajando con clases, atributos, métodos, encapsulamiento, reutilización de código y representación gráfica.
