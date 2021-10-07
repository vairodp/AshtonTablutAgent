from abc import ABCMeta, abstractmethod


class RingBuffer:
    _buffer = None


class Buffer(metaclass=ABCMeta):
    def __init__(self, max_size: int):
        self._max_size = max_size
        self._data = []

    def count(self, value):
        return self._data.count(value)

    def __len__(self):
        return len(self._data)

    @abstractmethod
    def __getitem__(self, i):
        """Get a list item"""
        pass

    @abstractmethod
    def append(self, value):
        pass


class FullBuffer(Buffer):
    """ class that implements a full buffer"""

    def __init__(self, max_size: int, data):
        super().__init__(max_size)
        self._data = data
        self._index = 0

    def append(self, value):
        """ Append an element overwriting the oldest one. """
        self._data[self._index] = value
        self._index = (self._index + 1) % self._max_size

    def __getitem__(self, i):
        """Get a list item"""
        return self._data[(self._index + i) % self._max_size]


class EmptyBuffer(Buffer):
    def __init__(self):
        super().__init__(0)

    def append(self, value):
        pass

    def __getitem__(self, i):
        raise 'Can not retrieve data from an empty buffer'


class NotYetFullBuffer(Buffer):
    def __init__(self, max_size: int, ring_buffer: RingBuffer):
        super().__init__(max_size)
        self._ring_buffer = ring_buffer

    def append(self, value):
        """append an element at the end of the buffer"""
        self._data.append(value)
        if len(self._data) == self._max_size:
            self._ring_buffer._buffer = FullBuffer(self._max_size, self._data)

    def __getitem__(self, i):
        """Get a list item"""
        return self._data[i]


class RingBuffer:
    """Class that implements a not-yet-full buffer """

    def __init__(self, max_size: int):
        self.max_size = max_size
        self._buffer = self._create_buffer()

    def append(self, value):
        """append an element at the end of the buffer"""
        self._buffer.append(value)

    def count(self, value):
        return self._buffer.count(value)

    def clear(self):
        self._buffer = NotYetFullBuffer(self.max_size, self)

    def __getitem__(self, i):
        """Get a list item"""
        return self._buffer[i]

    def __len__(self):
        return len(self._buffer)

    def __iter__(self):
        return RingBufferIterator(self)

    def __str__(self):
        return str(list(item for item in self))

    def _create_buffer(self):
        return NotYetFullBuffer(self.max_size, self) if self.max_size > 0 else EmptyBuffer()


class RingBufferIterator:
    def __init__(self, buffer: RingBuffer):
        self._buffer = buffer
        self._index = 0

    def __next__(self):
        ''''Returns the next value from team object's lists '''
        if self._index == len(self._buffer):
            raise StopIteration

        result = self._buffer[self._index]
        self._index += 1
        return result
