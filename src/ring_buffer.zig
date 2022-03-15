
pub fn RingBuffer(comptime T: type, comptime n: usize) type {
    return struct {
        arr : [n] T,
        index : usize = 0,

        const Self = *@This();

        pub fn insert(self: Self, val: T) void {
            defer self.index = (self.index + 1) % n;
            self.arr[self.index] = val;
        }

        pub fn get(self: Self, at : usize) T {
            return self.arr[(self.index + at) % n];
        }
    };
}

