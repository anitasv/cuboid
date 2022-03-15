const types = @import("types.zig");

fn CubeIter(comptime SrcIter: type, comptime dims: usize) type {

    return struct {
        iter: SrcIter,

        pub fn next(self: *@This()) ?types.Cuboid(dims) {
            var i : usize = 0;
            var segments : [dims]types.Segment = undefined;

            while (i < dims) : (i += 1) {
                if (self.iter.next()) |term| {
                    const start = term % 10000;
                    segments[i].start = start;
                }
            }

            i = 0;
            while (i < dims) : (i += 1) {
                if (self.iter.next()) |term| {
                    const width = term % 399;
                    segments[i].end = segments[i].start + (1 + width);
                }
            }

            return types.Cuboid(dims) {
                .segments = segments
            };
        }
    };
}

pub fn cuboidSeq(iter: anytype, comptime dims : usize) 
        CubeIter(@TypeOf(iter), dims) {
    return CubeIter(@TypeOf(iter), dims) {
        .iter = iter
    };
}


