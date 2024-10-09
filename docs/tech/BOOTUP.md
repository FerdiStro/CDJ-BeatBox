# Boot list
- org.main.Main.main()          -> Main class start Programm
- org.main.settings.Settings    ->  Settings class which load all config and set Settings <br>
    -> Load MidiControllerSettings <br>
  - org.main.midi.MidiController -> Init grpc to python for color switch und set up communication (transmitter and receiver) <br>
    -> Set Transmitter for MidiController
- org.main.BeatBoxWindow ->  Frame class for vis components
- org.main.settings.SettingsWindow -> Frame for Settings 
  - org.main.settings.graphics.CustomDropdown -> Dropdown Menu for midi-settings
