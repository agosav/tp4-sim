/* eslint-disable react/prop-types */
import React from "react";
import { Modal, Table, Button } from "react-bootstrap";

const round = (num, decimales) => {
    if (num === 0) return 0;
    return parseFloat(num).toFixed(decimales);
}

const ClientesModal = ({ show, handleClose, clientes }) => {
  return (
    <Modal show={show} onHide={handleClose} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Clientes</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {clientes.length > 0 ? (
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>ID</th>
                <th>Estado</th>
                <th>Acumulador Tiempo Espera</th>
                <th>Nombre Peluquero</th>
              </tr>
            </thead>
            <tbody>
              {clientes.map((cliente) => (
                <tr key={cliente.id}>
                  <td>{cliente.id}</td>
                  <td>{cliente.estado}</td>
                  <td>{round(cliente.acumuladorTiempoEspera, 4)}</td>
                  <td>{cliente.peluquero.nombre}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <h3>NO HAY CLIENTES EN ESTA ITERACION</h3>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          Cerrar
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ClientesModal;
