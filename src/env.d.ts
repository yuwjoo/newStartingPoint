/// <reference types="vite/client" />

import { string } from "vue-types";

declare module "*.vue" {
  import { defineComponent } from "vue";
  const Component: ReturnType<typeof defineComponent>;
  export default Component;
}

declare module '*.less' {
  const classes: readonly string
  export default classes
  declare module '*.less'
}

interface ImportMetaEnv {
  readonly VITE_API_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
  readonly glob: any
}
