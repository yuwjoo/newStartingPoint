import { ref, watch, watchEffect } from "vue";
import { useDraggable } from "@vueuse/core";

export default function (draggableNode) {
    
    const { x, y, isDragging, ...arg } = useDraggable(draggableNode);

    const startX = ref(0);
    const startY = ref(0);
    const startedDrag = ref(false);
    const transformX = ref(0);
    const transformY = ref(0);
    const preTransformX = ref(0);
    const preTransformY = ref(0);
    const dragRect = ref({
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
    });
    watch([x, y], () => {
        if (!startedDrag.value) {
            startX.value = x.value;
            startY.value = y.value;
            const bodyRect = document.body.getBoundingClientRect();
            const titleRect = draggableNode.getBoundingClientRect();
            dragRect.value.left = 50 - titleRect.width;
            dragRect.value.right = bodyRect.width - 100;
            dragRect.value.bottom = bodyRect.height - titleRect.height;
            preTransformX.value = transformX.value;
            preTransformY.value = transformY.value;
        }
        startedDrag.value = true;
    });
    watch(isDragging, () => {
        if (!isDragging) {
            startedDrag.value = false;
        }
    });
    watchEffect(() => {
        if (startedDrag.value) {
            transformX.value =
            preTransformX.value +
            Math.min(Math.max(dragRect.value.left, x.value), dragRect.value.right) -
            startX.value;
            transformY.value =
            preTransformY.value +
            Math.min(Math.max(dragRect.value.top, y.value), dragRect.value.bottom) -
            startY.value;
        }
    });
    return {
        transformX,
        transformY
    }
}