# ChessMate  

**ChessMate** is an Android app designed to teach beginners how to play chess. Developed as part of my BSc Computer Science thesis, the app provides a structured approach to learning through puzzles, lessons, and games against a custom-built chess bot.  

## Features  

- **Structured Learning**:  
  - **Puzzles**: Beginner, intermediate, and advanced puzzles to practice many different positions and situations.  
  - **Lessons**: Step-by-step guidance on chess basics, tactical concepts, openings and a coordinates gamemode.  
- **Chess Bot**:  
  - Custom-built bot with ~1200 ELO (based on testing against stockfish).  
  - Implements **bitboards** for efficient board representation and fast move generation.  
  - Uses **min-max** algorithm with **alpha-beta pruning** for gameplay.  
  - The search algorithm is extended with features to enchance the bots decision making:
    - **Move Prioritization**
    - **SEE (Static Exchange Evaluation)**  
    - **MVV LVA (Most Valuable Victim, Least Valuable Attacker)**  
    - **PESTO (Piece-Square Tables)** for position evaluation.  
    - Selective deepening for moves that give checks.
- **FEN**:
  - The app supports FEN string input to start a game from any legal position.
  - It includes a built-in chessboard editor that allows the user set up any position and copy it's FEN string. 
- **Modern Design**:
  - A clean and user-friendly interface.
  - Customizable with different app themes, chessboard and chess piece styles.  

## Technical Details  

- **Language**: Kotlin  
- **Minimum API Level**: 26 (Android 8.0)
- **Compile SDK Version**: 34
- **Target SDK Version**: 33 (Android 13)
- **Android Gradle Plugin Version**: 8.4.0
- **Kotlin Gradle Plugin Version**: 1.9.0
- **Kotlin JVM Target**: 1.8
- **Gradle Version**: 8.6
