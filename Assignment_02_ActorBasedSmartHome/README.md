# Lab 2 – Actors


In the Actormodel there are Actors which can send messages to other Actors. The Actors react on these messages and can change their behaviour depending on the message they receive.

NOTE: We added equals and hashcode to the ReadTemperature and ReadWeatherCondition because otherwise the tests would fail because of the different identity of the instantiated command class.

For more information, check the documentation in the docs folder.

## How to start the project
To start the project open it with IntelliJ (We only tested it with IntelliJ) and run the main method in the HomeAutomationSystem class. Then the command line interface will start up and ask you for your name. Enter your name and the available commands will be presented to you. The commands are structured in a way where the command line interface will guide the user through every command. In case of an invalid command, it will show the available commands.
Structure	 
We divided the structure into UI, Environment and Devices. The UI contains the UIActor which provides a command line interface to interact with the home automation system.
The Environment contains the EnvironmentActor to simulate the temperature and weather. For the weather we created an Enum. The Devices are the sensors, blind, mediastation, aircondition and the fridge. The Fridge spawns an OrderProcessor and has it own sensors for weight and space.

## Actors
### Environment	 
The environment actor sends periodically messages to itself. It changes the temperature and the weather automatically and stores it internally in variables. The TemperatureSensor and WeatherSensor ask the EnvironmentActor for the current value (Request-Response).

## AirCondition
After the TemperatureSensor receives a temperature it sends it to the AirCondition which reacts depending on the measured temperature. If the temperature is above 20°C it starts cooling off and if it is lower than it stops.

## Blind
The WeatherSensor tells the blind the current weather after it receives it from the environment. Additionally, the MediaStation tells the blind if a movie is playing or not. The blind closes if a movie is playing or if the weather is sunny. Otherwise, it will be opened. The Blind saves the last state of the MediaStation and the last received weather condition, so if the MediaStation tells the Blinds that it stopped playing a movie it will only open if the current weather is not sunny.

## Fridge
The Fridge is a special actor as it spawns a new OrderProcessor if it receives an order. The OrderProcessor handles the order process and asks the WeightSensor and SpaceSensor if there is enough left for the ordered product. It tells then the fridge that the order was approved or denied. In case the order was approved, the fridge adds the order to the order history and the product to its product list.

When a product is consumed, the fridge checks if the product is currently in the fridge. If the product is currently stored it tells the sensors that a product is consumed so they update the current weight and space.
