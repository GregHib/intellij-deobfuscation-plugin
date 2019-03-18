# Intellij Plugins

Plugins made for the jetbrains intellij platform.

## Deobfuscation

Tools to support de-obfuscated code refactoring

| Name | Description | Example | After |
|--|--|--|--|
| Pointless Bitwise Comparator | Simplifies bitwise comparators | `(i ^ 0xffffffff) <= -1` | | `i >= 0` |
| Inverse Else Comparator | Reverses negative if else statements | `if (i != 1) {` | `if (i == 1) {` |
| Inline Nested Else | Inlines else blocks containing if statements | `else { if(...) {` | `else if(...) {` |

## Setup -  Install plugin from disk
Taken from jetbrains managing-plugins guide
[https://www.jetbrains.com/help/idea/managing-plugins.html](https://www.jetbrains.com/help/idea/managing-plugins.html)

1.  In the **Settings/Preferences** dialog (Ctrl+Alt+S), select **Plugins**.
2.  In the **Plugins** dialog, click  ![The cogwheel icon](https://www.jetbrains.com/help/img/idea/2018.3/artwork.studio.icons.logcat.toolbar.settings@2x.png)  and then click **Install Plugin from Disk**.
3.  Select the plugin archive file and click **OK**.
4.  Click **OK** to apply the changes and restart the IDE if prompted.

## Usage

### Highlighting
If code in your project code matches one of the tools it will be highlighted
![code highlight image](https://i.imgur.com/RpHrpvh.png)

Open the intention actions and quick-fixes menu by pressing the light bulb icon next to the highlighted code (Alt + Enter)

The intention menu can be used to apply a single quick-fix or by pressing the right arrow key additional options can be selected such as fixing all in file, module or project.

### Search
Inspections can also be ran by name

`Analyze -> Run inspection by name` (Ctrl + Shift + Alt + I)
