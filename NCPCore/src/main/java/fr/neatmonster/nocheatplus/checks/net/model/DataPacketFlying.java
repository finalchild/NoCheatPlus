package fr.neatmonster.nocheatplus.checks.net.model;

public class DataPacketFlying extends DataLocation {

    // TODO: Use MAX_VALUE for not set doubles/floats?
    // TODO: Consider private + access methods.
    // TODO: Consider AlmostBoolean for fault tolerance ?
    // TODO: hashCode + equals.

    public final boolean onGround;
    public final boolean hasPos;
    public final boolean hasLook;
    public final long time;

    public DataPacketFlying(boolean onGround, long time) {
        super(0, 0, 0, 0, 0);
        this.onGround = onGround;
        hasPos = false;
        hasLook = false;
        this.time = time;
    }

    public DataPacketFlying(boolean onGround, float yaw, float pitch, long time) {
        super(0, 0, 0, yaw, pitch);
        this.onGround = onGround;
        hasPos = false;
        hasLook = true;
        this.time = time;
    }

    public DataPacketFlying(boolean onGround, double x, double y, double z, long time) {
        super(x, y, z, 0, 0);
        this.onGround = onGround;
        hasPos = true;
        hasLook = false;
        this.time = time;
    }

    public DataPacketFlying(boolean onGround, double x, double y, double z, float yaw, float pitch, long time) {
        super(x, y, z, yaw, pitch);
        this.onGround = onGround;
        hasPos = true;
        hasLook = true;
        this.time = time;
    }

    /**
     * Test if this is the same location (coordinates + pitch + yaw) as the
     * other given data.
     * 
     * @param other
     * @return False if either packet is lacking hasPos or hasLook, or if any of
     *         x/y/z/pitch/yaw differ, true if all of those match.
     */
    public boolean containsSameLocation(final DataPacketFlying other) {
        return hasPos && other.hasPos && hasLook && other.hasLook && isSameLocation(other);
    }

    /**
     * Test if this packet has pos and look and has the same coordinates and looking direction as the other one.
     * @param other
     * @return
     */
    public boolean containsSameLocation(final DataLocation other) {
        return hasPos && hasLook && isSameLocation(other);
    }

    /**
     * Quick test if position and look is contained and match.
     * 
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     * @return
     */
    public boolean matches(final double x, final double y, final double z, final float yaw, final float pitch) {
        return hasPos && hasLook && isSameLocation(x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("Flying(ground=");
        builder.append(onGround);
        if (hasPos) {
            builder.append(",x=");
            builder.append(x);
            builder.append(",y=");
            builder.append(y);
            builder.append(",z=");
            builder.append(z);
        }
        if (hasLook) {
            builder.append(",pitch=");
            builder.append(pitch);
            builder.append(",yaw=");
            builder.append(yaw);
        }
        // Skip time for now.
        builder.append(")");
        return builder.toString();
    }

}
