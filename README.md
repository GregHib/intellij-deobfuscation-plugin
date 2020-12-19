# Intellij Plugins

Plugins made for the jetbrains intellij platform.

## Deobfuscation

Tools to support de-obfuscated code refactoring

| Name | Description | Example | After |
|--|--|--|--|
| Pointless Bitwise Comparator | Simplifies bitwise comparators | `(i ^ 0xffffffff) <= -1` | | `i >= 0` |
| Inverse Else Comparator | Reverses negative if else statements | `if (i != 1) {` | `if (i == 1) {` |
| Inline Nested Else | Inlines else blocks containing if statements | `else { if(...) {` | `else if(...) {` |

## Setup
This plugin can now be found on the [Jetbrains Plugin Repository](https://plugins.jetbrains.com/plugin/14101-greg-s-deobsfuscation-tools)!

See [Managing Plugins](https://www.jetbrains.com/help/idea/managing-plugins.html) for further setup instructions.

## Usage

### Highlighting
If code in your project code matches one of the tools it will be highlighted
![code highlight image](https://i.imgur.com/RpHrpvh.png)

Open the intention actions and quick-fixes menu by pressing the light bulb icon next to the highlighted code (Alt + Enter)

The intention menu can be used to apply a single quick-fix or by pressing the right arrow key additional options can be selected such as fixing all in a file, module or project.

### Search
Inspections can also be ran by name

`Analyze -> Run inspection by name` (Ctrl + Shift + Alt + I)
