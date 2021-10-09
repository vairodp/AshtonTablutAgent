import random
import string


def random_string(length) -> str:
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))


def write_string(socket, text: str):
    encoded = text.encode('utf-8')
    socket.sendall(len(encoded).to_bytes(4, byteorder='big'))
    socket.sendall(encoded)


def read_string(socket):
    length = int.from_bytes(recvall(socket, 4), byteorder='big')
    return recvall(socket, length).decode('utf-8')


def recvall(sock, n):
    # Helper function to recv n bytes or return None if EOF is hit
    data = bytearray()
    while len(data) < n:
        packet = sock.recv(n - len(data))
        if not packet:
            return None
        data.extend(packet)
    return data
