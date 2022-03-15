pub const Segment = struct {
    start: u32,
    end: u32,

    pub fn volume(self: @This()) u32 {
        return self.end - self.start;
    }
};

pub fn Cuboid(comptime dims: usize) type {
    return struct {
        segments : [dims] Segment,

        pub fn volume(self: @This()) u128 {
            var total_volume : u128 = 1;
            for (self.segments) |segment| {
                total_volume *= segment.volume();
            }
            return total_volume;
        }
    };
}

pub fn PhasedCuboid(comptime dims: usize) type {
    return struct {
        phase: bool,
        cuboid: Cuboid(dims),

        pub fn volume(self: @This()) i128 {
            const cube_vol: u128 = self.cuboid.volume();
            if (self.phase) {
                return @intCast(i128, cube_vol);
            } else {
                return -(@intCast(i128, cube_vol));
            }
        }
    };
}
