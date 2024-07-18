# https://forum.arturia.com/index.php?topic=93714.msg152899#msg152899
# n is the pad number, 0 to F, corresponding to Pad1 to Pad16
# cc is the color:
# 00 - black
# 01 - red
# 04 - green
# 05 - yellow
# 10 - blue
# 11 - magenta
# 14 - cyan
# 7F - white

import mido
import argparse


def main(pad, color):
    print("Pad:", pad)
    print("Color:", color)

    pad_number = int(pad, 16) - 1  # Assuming pad is in hex from 1 to F
    pad_value = 0x70 + pad_number
    color_value = int(color, 16)

    print("Pad (int):", pad_value)
    print("Color (int):", color_value)

    midi_out = mido.open_output('Arturia MiniLab mkII')

    sysex_data = [0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, pad_value, color_value]

    midi_out.send(mido.Message('sysex', data=sysex_data))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Send a sysex message to the Arturia MiniLab mkII.')
    parser.add_argument('pad', type=str, help='Pad id (1-16) in hex (0-9, A-F)')
    parser.add_argument('color', type=str, help='Color code (00-7F) in hex')

    args = parser.parse_args()

    main(args.pad, args.color)
