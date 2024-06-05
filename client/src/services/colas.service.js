import axios from 'axios';

const simular = async (data) => {
    try {
        const response = await axios.post('http://localhost:8080/colas/simular', data);
        return response.data;
    } catch (error) {
        return error.response.data;
    }
}

const colasServices = {
    simular
}

export { colasServices }