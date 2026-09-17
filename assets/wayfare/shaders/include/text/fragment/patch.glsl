#ifdef IS_GUI
if (renderCursor > 0.5) {
    vec4 cursorColor = texture(Sampler0, cursorUV);
    if (cursorColor.a < 0.1) {
        discard;
    }
    fragColor = cursorColor;
}
#endif
