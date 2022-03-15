const std = @import("std");
const types = @import("types.zig");

fn intersectSegment(a: types.Segment, b: types.Segment) ?types.Segment {
    const c_start = std.math.max(a.start, b.start);
    const c_end = std.math.min(a.end, b.end);
    if (c_start < c_end) {
        return types.Segment {
            .start = c_start,
            .end = c_end
        };
    } else {
        return null;
    }
}

fn intersectCuboid(comptime dims: usize, 
    a: types.Cuboid(dims), 
    b: types.Cuboid(dims)) 
    ?types.Cuboid(dims) {

    var c_segments : [dims]types.Segment = undefined;

    for (c_segments) |*seg, i| {
        const a_seg = a.segments[i];
        const b_seg = b.segments[i];
        const c_seg_opt = intersectSegment(a_seg, b_seg);
        if (c_seg_opt) |c_seg| {
            seg.* = c_seg;
        } else {
            return null;
        }
    }
    return types.Cuboid(dims) {
        .segments = c_segments,
    };
}

pub fn intersectPhased(comptime dims: usize, 
    a: types.PhasedCuboid(dims), 
    b_cuboid: types.Cuboid(dims)) 
    ?types.PhasedCuboid(dims) {

    const c_cuboid_opt = intersectCuboid(dims, a.cuboid, b_cuboid);

    if (c_cuboid_opt) |c_cuboid| {
        const c_phase : bool = !a.phase;
        return types.PhasedCuboid(dims) {
            .phase = c_phase,
            .cuboid = c_cuboid
        };
    } else {
        return null;
    }
}

// Testing functions only, don't expose these!
fn segment(start: u32, end: u32) types.Segment {
    return types.Segment {
        .start = start,
        .end = end,
    };
}

const nilSegment = @as(?types.Segment, null);

fn cuboid(s1: types.Segment, s2 : types.Segment, s3: types.Segment) types.Cuboid(3) {
    return types.Cuboid(3) {
        .segments = [3]types.Segment{s1, s2, s3}
    };
}


const nilCuboid = @as(?types.Cuboid(3), null);

fn positivePhased(cube : types.Cuboid(3)) types.PhasedCuboid(3) {
    return types.PhasedCuboid(3) {
        .cuboid = cube,
        .phase = true
    };
}
fn negativePhased(cube : types.Cuboid(3)) types.PhasedCuboid(3) {
    return types.PhasedCuboid(3) {
        .cuboid = cube,
        .phase = false
    };
}

const nilPhased = @as(?types.PhasedCuboid(3), null);


test "Segment intersection non-overlap" {
    try std.testing.expectEqual(nilSegment, 
        intersectSegment(segment(1, 3), segment(4, 5)));
    try std.testing.expectEqual(nilSegment, 
        intersectSegment(segment(4, 5), segment(1, 3)));
}


test "Segment intersection overlap" {
    try std.testing.expectEqual(@as(?types.Segment, segment(2, 3)), 
        intersectSegment(segment(1, 3), segment(2, 5)));
    try std.testing.expectEqual(@as(?types.Segment, segment(2, 3)), 
        intersectSegment(segment(2, 5), segment(1, 3)));
}



test "Cuboid intersection non-overlap" {
    const s12 = segment(1, 2);
    const s13 = segment(1, 3);
    const s23 = segment(2, 3);
    const s24 = segment(2, 4);
    const s34 = segment(3, 4);
    const s45 = segment(4, 5);

    // all 3 non overlap
    try std.testing.expectEqual(nilCuboid,
        intersectCuboid(3,
            cuboid(s12, s23, s45), 
            cuboid(s23, s34, s12)));

    // mismatch first
    try std.testing.expectEqual(nilCuboid,
        intersectCuboid(3,
            cuboid(s12, s23, s24), 
            cuboid(s23, s24, s13)));

    // mismatch second
    try std.testing.expectEqual(nilCuboid,
        intersectCuboid(3,
            cuboid(s12, s23, s24), 
            cuboid(s13, s34, s13)));

    // mismatch third
    try std.testing.expectEqual(nilCuboid,
        intersectCuboid(3,
            cuboid(s12, s23, s45), 
            cuboid(s13, s24, s12)));

}


test "Cuboid intersection overlap" {
    const s13 = segment(1, 3);
    const s23 = segment(2, 3);
    const s24 = segment(2, 4);
    const s34 = segment(3, 4);
    const s35 = segment(3, 5);
    const s45 = segment(4, 5);
    const s46 = segment(4, 6);

    try std.testing.expectEqual(
        @as(?types.Cuboid(3), cuboid(s23, s34, s45)),
        intersectCuboid(3,
            cuboid(s13, s24, s35), 
            cuboid(s24, s35, s46)));
}

test "Phased cuboid intersect non-overlap" {
    const s12 = segment(1, 2);
    const s23 = segment(2, 3);
    const s34 = segment(3, 4);
    const s45 = segment(4, 5);

    // all 3 non overlap
    try std.testing.expectEqual(nilPhased,
        intersectPhased(3,
            positivePhased(cuboid(s12, s23, s45)),
            cuboid(s23, s34, s12)));
}

test "Positive phased cuboid intersect overlap" {
    const s13 = segment(1, 3);
    const s23 = segment(2, 3);
    const s24 = segment(2, 4);
    const s34 = segment(3, 4);
    const s35 = segment(3, 5);
    const s45 = segment(4, 5);
    const s46 = segment(4, 6);

    try std.testing.expectEqual(
        @as(?types.PhasedCuboid(3), 
            negativePhased(cuboid(s23, s34, s45))),
        intersectPhased(3,
            positivePhased(cuboid(s13, s24, s35)), 
            cuboid(s24, s35, s46)));

}

test "Negative phased cuboid intersect overlap" {
    const s13 = segment(1, 3);
    const s23 = segment(2, 3);
    const s24 = segment(2, 4);
    const s34 = segment(3, 4);
    const s35 = segment(3, 5);
    const s45 = segment(4, 5);
    const s46 = segment(4, 6);

    try std.testing.expectEqual(
        @as(?types.PhasedCuboid(3), 
            positivePhased(cuboid(s23, s34, s45))),
        intersectPhased(3,
            negativePhased(cuboid(s13, s24, s35)), 
            cuboid(s24, s35, s46)));
}