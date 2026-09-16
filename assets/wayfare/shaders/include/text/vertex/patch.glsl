#ifdef IS_GUI
// render cursor
int corner = gl_VertexID % 4;
renderCursor = texelFetch(Sampler0, ivec2(0, 0), 0).rgb == vec3(234.0, 123.0, 213.0) / 255.0 ? 1.0 : 0.0;
if (renderCursor > 0.5) {
    // calculate position offset
    float width = abs(ProjMat[0][0]) * 8.0;
    float height = abs(ProjMat[1][1]) * 8.0;
    vec2 offset = vec2(-width / 2.0, height / 2.0);
    // calculate texture offsets
    vec2 textureSize = vec2(textureSize(Sampler0, 0));
    vec2 currentPixel = UV0 * textureSize;
    vec2 cornerPixel = currentPixel;
    vec2 pixelOffset = vec2(1.0, 1.0);
    // determine based on quad corner
    if (corner == 1) {
        offset = vec2(-width / 2.0, -height / 2.0);
        cornerPixel = currentPixel - vec2(0.0, 256.0);
        pixelOffset = vec2(1.0, 9.0);
    } else if (corner == 2) {
        offset = vec2(width / 2.0, -height / 2.0);
        cornerPixel = currentPixel - vec2(256.0, 256.0);
        pixelOffset = vec2(9.0, 9.0);
    } else if (corner == 3) {
        offset = vec2(width / 2.0, height / 2.0);
        cornerPixel = currentPixel - vec2(256.0, 0.0);
        pixelOffset = vec2(9.0, 1.0);
    }
    // get x and y from input color
    float red = floor(Color.r * 255.0 + 0.5);
    float green = floor(Color.g * 255.0 + 0.5);
    float blue = floor(Color.b * 255.0 + 0.5);
    float x = mix(-1.0, 1.0, (red * 16.0 + floor(blue / 16.0)) / 4095.0);
    float y = mix(1.0, -1.0, (green * 16.0 + mod(blue, 16.0)) / 4095.0);
    // apply offsets
    gl_Position = vec4(x + offset.x, y + offset.y, gl_Position.z, 1.0);
    cursorUV = (cornerPixel + pixelOffset) / textureSize;
}
#endif
