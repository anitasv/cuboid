const std = @import("std");
const iter = @import("iterators.zig");
const fib = @import("fibonacci.zig");
const gc = @import("gen_cuboids.zig");
const solver = @import("solver.zig");

pub fn main() anyerror!void {
    const dims = 3;  // Use 3 dimensional cuboids!
    const num_cuboids = 50000; // How many cuboids to generate.
    var fi = fib.FibIter.init();  // Used Lagged Fibonacci as random input
    var ci = gc.cuboidSeq(fi, dims); // Generate cuboids
    if (solver.solve(dims, &ci, num_cuboids)) |answer| {
        std.debug.print("Solution for {} cuboids: {}", .{num_cuboids, answer});
    } else |err| {
        std.debug.print("Error: {}", .{err});
    }
}
