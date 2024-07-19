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

import grpc
from concurrent import futures
import time
import MidiService_pb2
import MidiService_pb2_grpc


class MidiServiceServicer(MidiService_pb2_grpc.MidiServiceServicer):
    def activateColor(self, request, context):
        response = MidiService_pb2.ActivateColorResponse()

        pad_hex = request.pad
        color_hex = request.color

        pad_number = int(pad_hex, 16) - 1
        pad_value = 0x70 + pad_number
        color_value = int(color_hex, 16)

        print("Pad (int):", pad_value)
        print("Color (int):", color_value)



        midi_out = mido.open_output('Arturia MiniLab mkII')

        sysex_data = [0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, pad_value, color_value]

        midi_out.send(mido.Message('sysex', data=sysex_data))
        response.ok = True
        return response


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    MidiService_pb2_grpc.add_MidiServiceServicer_to_server(MidiServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started on port 50051")
    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()

    # parser = argparse.ArgumentParser(description='Send a sysex message to the Arturia MiniLab mkII.')
    # parser.add_argument('pad', type=str, help='Pad id (1-16) in hex (0-9, A-F)')
    # parser.add_argument('color', type=str, help='Color code (00-7F) in hex')
    #
    # args = parser.parse_args()
    #
    # switchColor(args.pad, args.color)
