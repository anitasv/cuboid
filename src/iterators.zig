
pub const RangeIter = struct {
    limit : usize,
    index : usize,

    pub const Data = usize;

    pub fn next(self: *RangeIter) ?usize {
        if (self.index < self.limit) {
            defer self.index += 1;
            return self.index;
        } else {
            return null;
        }
    }
};

pub fn range(limit : usize) RangeIter {
    return RangeIter {
        .limit = limit,
        .index = 0
    };
}

fn MapIter(comptime IterType: type, comptime R: type) type{
    return struct {
        const Self = *@This();

        srcIter : IterType,
        mapFn : fn (IterType.Data) R, 

        pub const Data = R;

        pub fn next(self: Self) ?R {
            if (self.srcIter.next()) |src| {
                return self.mapFn(src);
            } else {
                return null;
            }
        }
    };
}

pub fn map(iter: anytype, R: type, 
     mapFn : fn((@TypeOf(iter).Data)) R ) MapIter(@TypeOf(iter), R) {

    return MapIter(@TypeOf(iter), R) {
        .srcIter = iter,
        .mapFn = mapFn
    };
}