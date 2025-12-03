# JavaFX MVVM Example

## 🎯 Lab Overview
This lab is helps you understand the Model-View-ViewModel (MVVM) design pattern by developing a simple
Student Management System using Java and JavaFX.<br>
Example code is available in the lesson PDF file under the name MvvmJavaFXExample.zip.

## Running the Example Code
- Open Project in VS Code
- Ensure you have JavaFX libraries configured in your project build path.
- Ctr + Shift + ` to open the terminal and write "mvn javafx:run".

## Adding Lombok to dependency 
Lombok is a java library that provide setter and getter method without declaring it.
- Rename User class to Student class.
- In Student class remove setter and getter by written @Data, @NoArgsConstructor, @AllArgsConstructor.

## Add Email and Birthdate to Student
In Scenebuilder, I provide 2 more labels which is email and birthdate that allow to user to fill more information.
- In email label, user can fill their own email address and it will updated in listview after user click save.
- In birthdate label, user can fill their date of birth by clicking datepicker component in scenebuilder.

## Update UI, bindings, and events the new fields
- In UserController, we simply modify in setupDataBinding() function so when user click save it will store the data in listview.
- User can change the data from listview by double clicking on their data set and it will pop up back into filling label.
- We also modify some function in UserViewModel and UserController since it interact with each other related to data binding.

## Add Sort Feature
- Add Combobox next to clear form button and apply checkbox next to it for Ascending data by default and Descending data base on user preference.
- User can sort based on Fullname, email and date of birth.
- Once the user decided, it will update inside listview.

## Add Search Feature
- Use textfield component as a search feature.
- User can search based on their name, email and dateofbirth.
- The listview will updated based on user search.

## Student names of your group members
I have add Team member name by default which is shown when the programming is running
```java
public UserViewModel() {
        users.addAll(
                new Student("Hongnyheng", "Chin", "hongnyheng@gmail.com", LocalDate.of(2005, 12, 29)),
                new Student("Vireak", "Rith", "vireakrith@gmail.com", LocalDate.of(2005, 12, 25)));
    }
``` 