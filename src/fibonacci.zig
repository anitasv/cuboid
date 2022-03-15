const std = @import("std");
const rb = @import("ring_buffer.zig");

const modulo = 1000000;

fn modulo_add(a: u64, b: u64) u64 {
    return (a + b) % modulo;
}

fn modulo_sub(a: u64, b: u64) u64 {
    return (a + (modulo-b)) % modulo;
}

fn modulo_mult(a: u64, b: u64) u64 {
    return (a * b) % modulo;
}

fn fibHead(k: u32) u32 {
    const k1 : u64 = @intCast(u64, k);
    const k2 = modulo_mult(k1, k1);
    const k3 = modulo_mult(k2, k1);

    const t1 = 100003;
    const t2 = modulo_mult(200003, k1);
    const t3 = modulo_mult(300007, k3);
    const answer = modulo_add(modulo_sub(t1, t2), t3);

    return @intCast(u32, answer);
}

pub const FibIter = struct {
    ring : rb.RingBuffer(u32, 55),
    
    pub fn init() FibIter {
        var arr : [55]u32 = undefined;
        for (arr) |*val, k| {
            val.* = fibHead(@intCast(u32, 1 + k));
        }
        const fi = FibIter{
            .ring = rb.RingBuffer(u32, 55) { 
                .arr = arr,
            },
        };
        return fi;
    }

    pub fn next(self : *@This()) ?u32 {
        const ret = self.ring.get(0);
        const tail = (ret + (self.ring.get(31))) % modulo;
        self.ring.insert(tail);
        return ret;
    }
};


const first100 = [100]u64 {200007, 100053, 600183, 500439, 600863, 701497, 602383, 103563, 5079, 106973, 
209287, 112063, 615343, 519169, 623583, 728627, 634343, 140773, 47959, 155943, 264767, 174473, 685103, 
596699, 709303, 822957, 737703, 253583, 170639, 288913, 408447, 329283, 851463, 775029, 900023, 26487, 
954463, 483993, 415119, 547883, 682327, 618493, 156423, 96159, 237743, 381217, 326623, 874003, 823399, 
974853, 128407, 84103, 641983, 602089, 764463, 529290, 951516, 375212, 400462, 627350, 655960, 86376, 
518682, 552962, 789300, 827780, 268486, 711502, 756912, 4800, 55250, 508346, 964172, 22812, 284350, 
348870, 816456, 287192, 361162, 238593, 774473, 112915, 654045, 797989, 944873, 494823, 847965, 404425,
564329, 727803, 294973, 665965, 240905, 419919, 603133, 190673, 582665, 179235, 380509, 586613};

test "First 100" {
    var actual: [100]u64 = undefined;
    var fi = FibIter.init();

    for (actual) |*fib| {
        if (fi.next()) |val| {
            fib.* = val;
        } else {
            break;
        }
    }
    try std.testing.expectEqual(first100, actual);
}
