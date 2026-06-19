const { Client, Authenticator } = require('minecraft-launcher-core');
const launcher = new Client();
launcher.launch({
    authorization: Authenticator.getAuth('Player'),
    root: 'C:\Users\andre\AppData\Roaming\\.nova',
    version: {
        number: '26.1.2',
        type: 'custom',
        custom: '26.1.2'
    },
    memory: { min: '1G', max: '2G' }
}).then(() => console.log('success')).catch(e => console.error(e));
