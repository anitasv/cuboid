const std = @import("std");
const types = @import("types.zig");
const intersect = @import("intersect.zig");

pub fn addCuboid(comptime dims: usize, 
    space: *std.ArrayList(types.PhasedCuboid(dims)), 
    cuboid : types.Cuboid(dims)) !void {

    const current_length = space.items.len;
    var i: usize = 0;
    while (i < current_length) : (i += 1) {
        const phased = space.items[i];
        const inter_opt = intersect.intersectPhased(dims, phased, cuboid);
        if (inter_opt) |inter| {
            try space.append(inter);
        }
    }
    try space.append(types.PhasedCuboid(dims) {
        .phase = true,
        .cuboid = cuboid,
    });
}

pub fn solve(comptime dims: usize, iter: anytype, cuboids: u64) !u128 {

    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    var allocator = arena.allocator();

    var space = std.ArrayList(types.PhasedCuboid(dims)).init(allocator);

    var i: usize = 0;
    while (iter.next()) |cuboid| : (i += 1) {
        if (i >= cuboids) {
            break;
        }
        try addCuboid(dims, &space, cuboid);
    }

    var total_vol: i128 = 0;
    for (space.items) |phased| {
        total_vol += phased.volume();
    }

    return @intCast(u128, total_vol);
}