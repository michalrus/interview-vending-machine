# interview-vending-machine

We’re creating an API for a [vending machine](https://en.wikipedia.org/wiki/Vending_machine). There are three options here:

* either the machine is purely virtual and this is just an exercise, and the API models the functions of a program that could be controlling a real vending machine,

* an API for a real (hardware) vending machine for keeping stats and reporting shortages,

* or an API for a real (hardware) machine used to control it. But that last option seems least sensible, as any user interaction would require a connection with the API server.

Considering this part of the instruction:

> […] The vending machine should respond to basic commands, such as `pay` and `select_products` […]

… I’m going to assume the assignment is about the first option — a purely virtual vending machine, and continue from here. To follow the process, see issues and PRs.
